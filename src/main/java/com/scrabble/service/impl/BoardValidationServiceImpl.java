package com.scrabble.service.impl;

import com.scrabble.model.Board;
import com.scrabble.model.Cell;
import com.scrabble.model.Move;
import com.scrabble.model.StatusEnum;
import com.scrabble.repository.BoardJpaRespository;
import com.scrabble.service.BoardValidationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BoardValidationServiceImpl implements BoardValidationService {
    @Autowired
    BoardJpaRespository boardJpaRespository;

    // check if board status is active
    public boolean isAvailableForPlay(Long boardId) {
        Board board = getBoardFromDb(boardId);
        if(board == null) {
            return false;
        }
        return StatusEnum.ACTIVE.equals(board.getStatus());
    }

    // all given moves(letters) should be either on same row or column
    public boolean isMoveOrdered(List<Move> moves) {

        boolean allRowEqual= true;
        boolean allColEqual = true;
        Integer posX = moves.get(0).getRow();
        Integer posY = moves.get(0).getColumn();
        for(Move move:moves) {
            if(!move.getColumn().equals(posY)) {
                allColEqual = false;
            }
            if(!move.getRow().equals(posX)) {
                allRowEqual = false;
            }
        }
        
        return allColEqual||allRowEqual;
    }

    // moves should not be on occupied cells
    // moves should be on cells that at least one neighbour is occupied(left-right-up-bottom)
    public boolean isMoveValid(Long boardId, List<Move> moves) {

        Board board = getBoardFromDb(boardId);
        Cell[][] cells = board.getCells();
        // initial move
        if(board.getPlayOrder() == 0) {
            return true;
        }
        
        return moves.stream().allMatch(move -> !isCellOccupied(move.getRow(), move.getColumn(), cells)) &&
                moves.stream().anyMatch(move -> isCellAvailableForInsert(move.getRow(), move.getColumn(), cells));
    }

    // check if cell occupied
    private boolean isCellOccupied(int row, int column, Cell[][] cells) {
        return  cells[row][column].getOccupied();
    }

    // moves should be on cells that at least one neighbour is occupied(left-right-up-bottom)
    private boolean isCellAvailableForInsert(int row, int column, Cell[][] cells) {
        return  cells[row+1][column].getOccupied() ||
                cells[row-1][column].getOccupied() ||
                cells[row][column+1].getOccupied() ||
                cells[row][column-1].getOccupied();
    }

    // board should exist
    public boolean isBoardExists(Long boardId) {
        Board board = getBoardFromDb(boardId);
        if(board == null) {
            return false;
        }
        return true;
    }

    private Board getBoardFromDb(Long boardId){
        return boardJpaRespository.findOne(boardId);
    }
}
