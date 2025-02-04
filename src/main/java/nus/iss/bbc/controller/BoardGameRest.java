package nus.iss.bbc.controller;

import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nus.iss.bbc.model.Game;
import nus.iss.bbc.service.BoardGameService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/game")
public class BoardGameRest {

    @Autowired
    private BoardGameService boardGameService;

    @GetMapping("/{game_id}")
    public ResponseEntity<Object> getGame(@PathVariable int game_id) {
        Game game = boardGameService.fetchGame(game_id);

         if (game == null) {
            String errorMessage = "{\"error\": \"Cannot find player " + game_id + "\"}";
    
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(errorMessage);
        }

        return ResponseEntity.ok(game);
        
    }
    
    @GetMapping("/{game_id}/reviews")
    public ResponseEntity<Object> getReview(@PathVariable int game_id) {

        Document comments = boardGameService.fetchGameWithComments(game_id);

        return ResponseEntity.ok(comments);

        //------------------------------------------

        // List<Comment> comments = boardGameService.fetchComment(game_id);
        
        // if (comments == null) {
        //     String errorMessage = "{\"error\": \"Cannot find player " + game_id + "\"}";
    
        //     return ResponseEntity
        //             .status(HttpStatus.NOT_FOUND)
        //             .contentType(MediaType.APPLICATION_JSON)
        //             .body(errorMessage);
        // }

        // return ResponseEntity.ok(comments);

    }
    
    @GetMapping("/highest")
    public ResponseEntity<Object> getHighestRating() {

        Document highest = boardGameService.fetchHighestRating();

        return ResponseEntity.ok(highest);
    }
    
    
}
