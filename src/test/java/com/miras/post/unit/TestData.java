package com.miras.post.unit;

import com.miras.post.model.Post;
import com.miras.post.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class TestData {

    public static Post getTestPost() {
        Post post = new Post();
        post.setId(UUID.fromString("10f72209-3d69-4e2a-809a-3f53909642d6"));
        post.setDescription("Test Content");
        post.setCreatedDate(Instant.parse("2024-05-24T08:07:37.511475Z"));
        post.setModifiedDate(Instant.parse("2024-05-24T08:07:37.511475Z"));
        post.setUser(getTestUser());
        return post;
    }

    public static User getTestUser() {
        User user = new User();
        user.setId(UUID.fromString("e9017c8f-0ee3-4c9f-ac5c-f53f05e152b0"));
        user.setFirstName("Miras");
        user.setLastName("Mahamood");
        user.setEmail("test@gmail.com");
        user.setPassword("testpassword");
        user.setCreatedDate(Instant.parse("2024-05-24T08:07:37.511623Z"));
        user.setModifiedDate(Instant.parse("2024-05-24T08:07:37.511623Z"));
        return user;
    }

    public static Page<Post> getAllUserPosts() {
        Post post = getTestPost();
        return new Page<>() {
            @Override
            public Iterator<Post> iterator() {
                return null;
            }

            @Override
            public int getTotalPages() {
                return 1;
            }

            @Override
            public long getTotalElements() {
                return 1;
            }

            @Override
            public <U> Page<U> map(Function<? super Post, ? extends U> converter) {
                return null;
            }

            @Override
            public int getNumber() {
                return 0;
            }

            @Override
            public int getSize() {
                return 50;
            }

            @Override
            public int getNumberOfElements() {
                return 1;
            }

            @Override
            public List<Post> getContent() {
                return List.of(post);
            }

            @Override
            public boolean hasContent() {
                return true;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public boolean isFirst() {
                return false;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public Pageable nextPageable() {
                return null;
            }

            @Override
            public Pageable previousPageable() {
                return null;
            }
        };
    }

    public static Slice<Post> getAllTestPosts() {
        Post post = getTestPost();
        return new Slice<>() {
            @Override
            public Iterator<Post> iterator() {
                return null;
            }

            @Override
            public int getNumber() {
                return 0;
            }

            @Override
            public int getSize() {
                return 50;
            }

            @Override
            public int getNumberOfElements() {
                return 1;
            }

            @Override
            public List<Post> getContent() {
                return List.of(post);
            }

            @Override
            public boolean hasContent() {
                return true;
            }

            @Override
            public Sort getSort() {
                return null;
            }

            @Override
            public boolean isFirst() {
                return true;
            }

            @Override
            public boolean isLast() {
                return false;
            }

            @Override
            public boolean hasNext() {
                return false;
            }

            @Override
            public boolean hasPrevious() {
                return false;
            }

            @Override
            public Pageable nextPageable() {
                return null;
            }

            @Override
            public Pageable previousPageable() {
                return null;
            }

            @Override
            public <U> Slice<U> map(Function<? super Post, ? extends U> converter) {
                return null;
            }
        };
    }
}
