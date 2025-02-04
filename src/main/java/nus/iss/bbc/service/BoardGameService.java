package nus.iss.bbc.service;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import nus.iss.bbc.model.Comment;
import nus.iss.bbc.model.Game;
import nus.iss.bbc.repository.BoardGameRepository;

@Service
public class BoardGameService {

    @Autowired
    private BoardGameRepository boardGameRepository;

    public Game fetchGame(int id){
        return boardGameRepository.fetchGame(id);
        
    }

    public List<Comment> fetchComment(int id){
        return boardGameRepository.fetchComments(id);
        
    }

    public Document fetchGameWithComments(int id){
        return boardGameRepository.fetchGameWithComments(id);
    }

    public Document fetchHighestRating(){
        return boardGameRepository.fetchHighestRating();
    }


    
}
