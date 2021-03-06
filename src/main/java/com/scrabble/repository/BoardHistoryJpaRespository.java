package com.scrabble.repository;

import com.scrabble.model.Board;
import com.scrabble.model.BoardHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

@Component
public interface BoardHistoryJpaRespository extends JpaRepository<BoardHistory, Long>{
    BoardHistory  findDistinctByBoardAndPlayOrder(Board board, Integer playOrder);
    
}
