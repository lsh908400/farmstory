package com.farmstory.repository.board;

import com.farmstory.entity.board.BoardEntity;
import com.farmstory.entity.user.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
    BoardEntity findByUser(UserEntity user);

    List<BoardEntity> findAllByBoardTypeAndBoardSection(String type, String section);

    List<BoardEntity> findAllByBoardTypeAndBoardSectionAndBoardTitleContainingOrderByBoardIdxDesc(String type, String section, String searchValue);

    Page<BoardEntity> findAllByBoardTypeAndBoardSectionAndBoardTitleContainingOrderByBoardIdxDesc(String type, String section, String searchValue, Pageable pageable);

    Page<BoardEntity> findAllByBoardTypeAndBoardSectionAndUserOrderByBoardIdxDesc(String type, String section, UserEntity userEntity, Pageable pageable);

    Page<BoardEntity> findAllByBoardTypeAndBoardSection(String type, String section, Pageable pageable);

    Page<BoardEntity> findAllByBoardTypeAndBoardSectionOrderByBoardIdxDesc(String type, String section, Pageable pageable);
}
