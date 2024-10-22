package com.stempo.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.stempo.exception.PermissionDeniedException;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BoardTest {

    private Board board;
    private User owner;
    private User admin;
    private User normalUser;

    @BeforeEach
    void setUp() {
        owner = mock(User.class);
        admin = mock(User.class);
        normalUser = mock(User.class);

        when(owner.getDeviceTag()).thenReturn("OWNER_DEVICE_TAG");
        when(admin.getDeviceTag()).thenReturn("ADMIN_DEVICE_TAG");
        when(admin.isAdmin()).thenReturn(true);
        when(normalUser.getDeviceTag()).thenReturn("USER_DEVICE_TAG");
        when(normalUser.isAdmin()).thenReturn(false);

        board = Board.builder()
                .id(1L)
                .deviceTag("OWNER_DEVICE_TAG")
                .category(BoardCategory.NOTICE)
                .title("Test Title")
                .content("Test Content")
                .fileUrls(Arrays.asList("file1", "file2"))
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    void 게시글이_정상적으로_생성되는지_확인한다() {
        // then
        assertThat(board.getId()).isEqualTo(1L);
        assertThat(board.getDeviceTag()).isEqualTo("OWNER_DEVICE_TAG");
        assertThat(board.getCategory()).isEqualTo(BoardCategory.NOTICE);
        assertThat(board.getTitle()).isEqualTo("Test Title");
        assertThat(board.getContent()).isEqualTo("Test Content");
        assertThat(board.getFileUrls()).containsExactly("file1", "file2");
        assertThat(board.getCreatedAt()).isNotNull();
    }

    @Test
    void 게시글_정보를_수정할_수_있다() {
        // given
        Board updateBoard = Board.builder()
                .category(BoardCategory.FAQ)
                .title("Updated Title")
                .content("Updated Content")
                .fileUrls(Arrays.asList("file3", "file4"))
                .build();

        // when
        board.update(updateBoard);

        // then
        assertThat(board.getCategory()).isEqualTo(BoardCategory.FAQ);
        assertThat(board.getTitle()).isEqualTo("Updated Title");
        assertThat(board.getContent()).isEqualTo("Updated Content");
        assertThat(board.getFileUrls()).containsExactly("file3", "file4");
    }

    @Test
    void 게시글_정보_수정시_null_값을_무시한다() {
        // given
        Board updateBoard = Board.builder().build();

        // when
        board.update(updateBoard);

        // then
        assertThat(board.getCategory()).isEqualTo(BoardCategory.NOTICE);
        assertThat(board.getTitle()).isEqualTo("Test Title");
        assertThat(board.getContent()).isEqualTo("Test Content");
        assertThat(board.getFileUrls()).containsExactly("file1", "file2");
    }

    @Test
    void 게시글_작성자인지_확인할_수_있다() {
        // then
        assertThat(board.isOwner(owner)).isTrue();
        assertThat(board.isOwner(admin)).isFalse();
        assertThat(board.isOwner(normalUser)).isFalse();
    }

    @Test
    void 게시글_수정_삭제_권한을_검증한다() {
        // 작성자 권한 검증
        assertDoesNotThrow(() -> board.validateAccessPermission(owner));

        // 관리자 권한 검증
        assertDoesNotThrow(() -> board.validateAccessPermission(admin));

        // 권한 없는 사용자 접근 시 예외 발생
        PermissionDeniedException exception = assertThrows(PermissionDeniedException.class,
                () -> board.validateAccessPermission(normalUser));
        assertThat(exception.getMessage()).isEqualTo("게시글을 수정/삭제할 권한이 없습니다.");
    }

    @Test
    void 공지사항인지_확인할_수_있다() {
        // then
        assertThat(board.isNotice()).isTrue();
    }

    @Test
    void 공지사항이_아닌경우_false를_반환한다() {
        // when
        board.setCategory(BoardCategory.FAQ);

        // then
        assertThat(board.isNotice()).isFalse();
    }

    @Test
    void 공지사항에_권한없는_사용자가_접근할_수_없다() {
        // when
        PermissionDeniedException exception = assertThrows(PermissionDeniedException.class,
                () -> board.validateAccessPermissionForNotice(normalUser));

        // then
        assertThat(exception.getMessage()).isEqualTo("공지사항 관리 권한이 없습니다.");
    }

    @Test
    void 공지사항에_관리자는_접근_가능하다() {
        // then
        assertDoesNotThrow(() -> board.validateAccessPermissionForNotice(admin));
    }
}
