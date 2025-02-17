package efub.session.blog.comment.service;

import efub.session.blog.comment.domain.Comment;
import efub.session.blog.comment.dto.CommentModifyRequestDto;
import efub.session.blog.comment.dto.CommentRequestDto;
import efub.session.blog.comment.repository.CommentRepository;
import efub.session.blog.member.domain.Member;
import efub.session.blog.member.service.MemberService;
import efub.session.blog.post.domain.Post;
import efub.session.blog.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final PostService postService;
    private final MemberService memberService;

    //댓글 작성
    public Long createComment(Long postId, CommentRequestDto requestDto) {
        Post post = postService.findPost(postId);
        Member writer = memberService.findMemberById(requestDto.getMemberId());
        return commentRepository.save(requestDto.toEntity(post, writer)).getCommentId();
    }

    //댓글 조회-ID별
    @Transactional(readOnly = true)
    public Comment findCommentById(Long commentId){
        return commentRepository.findById(commentId)
                .orElseThrow(()->new EntityNotFoundException(("존재하지 않는 댓글입니다. ID="+commentId)));
    }

    //댓글 목록 조회-작성자별
    @Transactional(readOnly = true)
    public List<Comment> findCommentListByWriter(Member writer){
        return commentRepository.findAllByWriter(writer);
    }

    //댓글 목록 조회-게시글별
    @Transactional(readOnly = true)
    public List<Comment> findCommentListByPost(Long postId){
        Post post=postService.findPost(postId);
        return commentRepository.findAllByPost(post);
    }

    //댓글 수정
    public void modifyComment(CommentRequestDto requestDto, Long commentId) {
        Comment comment = findCommentById(commentId);
        comment.modifyComment(requestDto.getContent());
    }

    //댓글 삭제
    public void deleteComment(Long commentId){
        Comment comment = findCommentById(commentId);
        commentRepository.delete(comment);
    }
}
