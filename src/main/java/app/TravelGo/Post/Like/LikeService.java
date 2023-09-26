package app.TravelGo.Post.Like;

import app.TravelGo.Post.Post;
import app.TravelGo.User.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class LikeService {
    private final LikeRepository likeRepository;

    @Autowired
    public LikeService(LikeRepository likeRepository) {
        this.likeRepository = likeRepository;
    }

    public boolean isPostLikedByUser(User user, Post post) {
        Like like = likeRepository.findByUserAndPost(user, post);
        return like != null;
    }

    public void likePost(User user, Post post) {
        Like like = new Like();
        like.setUser(user);
        like.setPost(post);
        likeRepository.save(like);
    }

    @Transactional
    public void unlikePost(User user, Post post) {
      likeRepository.deleteByUserAndPost(user, post);
    }

    public Long getLikesCountForPost(Post post) {
        return likeRepository.countByPost(post);
    }
}
