package de.holhar.spring.patterns.elasticsearch.rest;

import static java.util.Arrays.asList;
import static org.elasticsearch.index.query.QueryBuilders.matchQuery;
import static org.elasticsearch.index.query.QueryBuilders.regexpQuery;

import de.holhar.spring.patterns.elasticsearch.model.Article;
import de.holhar.spring.patterns.elasticsearch.model.Author;
import de.holhar.spring.patterns.elasticsearch.repository.ArticleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

  private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

  private final ElasticsearchOperations elasticsearchTemplate;
  private final ArticleRepository articleRepository;

  public IndexController(ElasticsearchOperations elasticsearchTemplate, ArticleRepository articleRepository) {
    this.elasticsearchTemplate = elasticsearchTemplate;
    this.articleRepository = articleRepository;
  }

  @PostMapping("/index/create")
  public void indexDocuments() {
    logger.info("--- Do fill index -> continue!");
    elasticsearchTemplate.indexOps(Article.class).create();

    Article article = new Article("Spring Data Elasticsearch");
    article.setAuthors(asList(new Author("John Smith"), new Author("John Doe")));
    articleRepository.save(article);
  }

  @GetMapping("/article/search/{word}")
  public Article findArticleByWordInTitle(@PathVariable("word") String word) {
    Query searchQuery = new NativeSearchQueryBuilder()
        .withFilter(regexpQuery("title", ".*" + word + ".*"))
        .build();

    SearchHits<Article> searchHits = elasticsearchTemplate.search(searchQuery, Article.class, IndexCoordinates.of("blog"));
    return searchHits.getSearchHit(0).getContent();
  }

  @PostMapping("/articles/search")
  public Page<Article> getArticlesByAuthor(@RequestBody SearchByAuthorRequest searchByAuthorRequest) {
    Assert.notNull(searchByAuthorRequest.getAuthor(), "Author param must be given");
    return articleRepository.findByAuthorsName(searchByAuthorRequest.getAuthor(), PageRequest.of(0, 10));
  }

  @PutMapping("/articles/article")
  public Article updateArticle(@RequestBody UpdateDocumentRequest updateDocumentRequest) {
    Assert.notNull(updateDocumentRequest.getOldTitle(), "Old title must be set");
    Assert.notNull(updateDocumentRequest.getNewTitle(), "New title must be set");

    Query searchQuery = new NativeSearchQueryBuilder()
        .withQuery(matchQuery("title", updateDocumentRequest.getOldTitle()).minimumShouldMatch("75%"))
        .build();

    SearchHits<Article> articleSearchHits = elasticsearchTemplate.search(searchQuery, Article.class, IndexCoordinates.of("blog"));
    Article article = articleSearchHits.getSearchHit(0).getContent();
    article.setTitle(updateDocumentRequest.getNewTitle());
    articleRepository.save(article);

    return article;
  }

  @DeleteMapping("/articles/article/{word}")
  public ResponseEntity<Void> deleteFirstArticleContainingWordInTitle(@PathVariable("word") String word) {
    Article articleByWordInTitle = this.findArticleByWordInTitle(word);
    articleRepository.delete(articleByWordInTitle);
    return ResponseEntity.ok().build();
  }
}
