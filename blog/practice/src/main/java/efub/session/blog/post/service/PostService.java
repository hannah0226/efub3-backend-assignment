package efub.session.blog.post.service;

import efub.session.blog.member.repository.MemberRepository;
import efub.session.blog.member.domain.Member;
import efub.session.blog.member.service.MemberService;
import efub.session.blog.post.domain.Post;
import efub.session.blog.post.dto.PostModifyRequestDto;
import efub.session.blog.post.dto.PostRequestDto;
import efub.session.blog.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class PostService {
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final MemberService memberService;

    public Post addPost(PostRequestDto requestDto){
        Member owner = memberRepository.findById(requestDto.getMemberId())
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 계정입니다."));
        return postRepository.save(
                Post.builder()
                        .anonymous(requestDto.getAnonymous())
                        .content(requestDto.getContent())
                        .owner(owner)
                        .build()
        );
    }

    public List<Post> findPostList() {
        return postRepository.findAll();
    }

    public Post findPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(()->new IllegalArgumentException("존재하지 않는 게시글 입니다."));
    }

    public void removePost(Long postId, Long memeberId) {
        Post post = postRepository.findByPostIdAndOwner_MemberId(postId,memeberId)
                .orElseThrow(()->new IllegalArgumentException("잘못된 접근입니다."));
        postRepository.delete(post);
    }

    public Post modifyPost(Long posId, PostModifyRequestDto requestDto) {
        Post post = postRepository.findByPostIdAndOwner_MemberId(posId,requestDto.getMemberId())
                .orElseThrow(()->new IllegalArgumentException("잘못된 접근입니다."));
        post.updatePost(requestDto);
        return post;
    }

    @Transactional(readOnly = true)
    public List<Post> findPostListByWriter(Long memberId){
        Member writer= memberService.findMemberById(memberId);
        return postRepository.findAllByOwner(writer);
    }
}
