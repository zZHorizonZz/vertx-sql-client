package io.vertx.tests.sqldsl.example;

import io.vertx.codegen.annotations.DataObject;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Review entity for product reviews.
 */
@DataObject
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @Column(name = "review_id")
    private Long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "rating", precision = 2, scale = 1, nullable = false)
    private BigDecimal rating;

    @Column(name = "title", length = 200)
    private String title;

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;

    @Column(name = "is_verified_purchase")
    private Boolean verifiedPurchase;

    @Column(name = "helpful_votes")
    private Integer helpfulVotes;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "product_id", referencedColumnName = "product_id", insertable = false, updatable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", insertable = false, updatable = false)
    private User user;

    // Default constructor
    public Review() {
        this.verifiedPurchase = false;
        this.helpfulVotes = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor
    public Review(Long productId, Long userId, BigDecimal rating, String title, String comment) {
        this();
        this.productId = productId;
        this.userId = userId;
        this.rating = rating;
        this.title = title;
        this.comment = comment;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getVerifiedPurchase() {
        return verifiedPurchase;
    }

    public void setVerifiedPurchase(Boolean verifiedPurchase) {
        this.verifiedPurchase = verifiedPurchase;
    }

    public Integer getHelpfulVotes() {
        return helpfulVotes;
    }

    public void setHelpfulVotes(Integer helpfulVotes) {
        this.helpfulVotes = helpfulVotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Review{" +
                "id=" + id +
                ", productId=" + productId +
                ", userId=" + userId +
                ", rating=" + rating +
                ", title='" + title + '\'' +
                ", verifiedPurchase=" + verifiedPurchase +
                ", helpfulVotes=" + helpfulVotes +
                ", createdAt=" + createdAt +
                '}';
    }
}