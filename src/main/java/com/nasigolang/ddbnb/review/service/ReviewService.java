package com.nasigolang.ddbnb.review.service;

import com.nasigolang.ddbnb.review.dto.ReviewDTO;
import com.nasigolang.ddbnb.review.entity.Review;
import com.nasigolang.ddbnb.review.repository.ReviewRepository;
import com.nasigolang.ddbnb.util.FileUploadUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.UUID;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ModelMapper modelMapper;

    @Value("${image.image-dir}")
    private String IMAGE_DIR;
    @Value("${image.image-url}")
    private String IMAGE_URL;

    private long reviewId;

    public ReviewService(ReviewRepository reviewRepository, ModelMapper modelMapper) {
        this.reviewRepository = reviewRepository;
        this.modelMapper = modelMapper;
    }

    //전체리뷰 조회
    public Page<ReviewDTO> findAllReview(Pageable page) {
        page = PageRequest.of(page.getPageNumber() <= 0 ? 0 : page.getPageNumber() - 1, 8, Sort.by("reviewId"));

        //        Page<Review> reviews = reviewRepository.findAll(pageable);
        return reviewRepository.findAll(page).map(review -> modelMapper.map(review, ReviewDTO.class));
    }

    /*일부 조회*/
    //    @Transactional
    public ReviewDTO findReviewById(long reviewId) {
        Review foundReview = reviewRepository.findById(reviewId).get();
        return modelMapper.map(foundReview, ReviewDTO.class);

    }

    @Transactional
    public void postReview(ReviewDTO newReview) {
        if(newReview.getReviewImage() != null) {
            String imageName = UUID.randomUUID().toString().replace("-", "");
            String replaceFileName = null;

            try {
                replaceFileName = FileUploadUtils.saveFile(IMAGE_DIR, imageName, newReview.getReviewImage());
                newReview.setReviewImageUrl(replaceFileName);
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        }
        reviewRepository.save(modelMapper.map(newReview, Review.class));

    }
}
