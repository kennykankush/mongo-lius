package nus.iss.bbc.repository;


import java.time.Instant;
import java.util.List;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationExpression;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LiteralOperators;
import org.springframework.data.mongodb.core.aggregation.LookupOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.UnwindOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import nus.iss.bbc.model.Comment;
import nus.iss.bbc.model.Game;

@Repository
public class BoardGameRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    public Game fetchGame(int id){

        Criteria criteria = Criteria.where("gid").is(id);
        Query query = Query.query(criteria);

        Game game = mongoTemplate.findOne(query, Game.class, BoardGameConstants.BOARDGAME_C_GAME);

        return game;
        
    }

    public List<Comment> fetchComments(int id){

        Criteria criteria = Criteria.where("gid").is(id);
        Query query = Query.query(criteria);

        List<Comment> comments = mongoTemplate.find(query,Comment.class,BoardGameConstants.BOARDGAME_C_COMMENTS);

        return comments;
        
    }

    public Document fetchGameWithComments(int id){
        Aggregation aggregation = Aggregation.newAggregation(Aggregation.match(Criteria.where("gid").is(id)), 
        Aggregation.lookup("comments", "gid", "gid", "reviews")
        );

        AggregationResults<Document> result = mongoTemplate.aggregate(aggregation, BoardGameConstants.BOARDGAME_C_GAME, Document.class);

        // return result.getMappedResults();

        Instant instant = Instant.now();

        Document results = result.getUniqueMappedResult();

        if (results != null){
            results.put("timestampede", instant);
            return results;

        }

        return results;
        
    }

//     db.game.aggregate([
//     {
//         $lookup: {
//             from: 'comments',
//             localField: 'gid',
//             foreignField: 'gid',
//             as: 'comments',
//             pipeline: [
//                 { $sort: { rating: -1 } },
//                 { $limit: 1 }
//             ]
//         }
//     },
//     {
//         $unwind: '$comments'
//     },
//     {
//         $project: {
//              "_id": 0,
//             "gid": 1,
//             "name": 1,
//             rating: "$comments.rating",
//             comment: "$comments.user",
//             review_id: "$comments.c_id",
//         }

//     },
    
//     {
//         $group: {
//             _id: null,
//             rating: {$first: "highest"}, //{$push: "highest"} would give me in an array
//             games: {$push: "$$ROOT"},
//             timestamp: {$first: "$$NOW"}
            
//         }
//     },
    
//     {
//         $project: {
//             "rating": 1,
//             "games": 1,
//             "timestamp":1 ,
//             "_id": 0,
     
//         }
//     }
    
// ]);

    public Document fetchHighestRating(){

        LookupOperation lookupComments = LookupOperation.newLookup()
        .from("comments")
        .localField("gid")
        .foreignField("gid")
        .pipeline(Aggregation.sort(Direction.DESC, "rating"), Aggregation.limit(1))
        .as("comments");

        AggregationOperation unwindComments = Aggregation.unwind("comments");

        ProjectionOperation projectField = Aggregation.project( 
            "gid","name")
            .and("comments.rating").as("rating")
            .and("comments.user").as("user")
            .and("comments.c_text").as("comment")
            .and("comments.c_id").as("review_id")
            .andExclude("_id");

        GroupOperation group = Aggregation.group()
        .first(LiteralOperators.Literal.asLiteral("highest")).as("rating") //super weird to get literal but i would imagine you'd hardcore this manually using a hashput into a document
        .push("$$ROOT").as("games")
        .first("$$NOW").as("timestamp");

        ProjectionOperation projectField2 = Aggregation.project("rating", "games", "timestamp")
        .andExclude("_id");

        Aggregation pipeline = Aggregation.newAggregation(lookupComments, unwindComments, projectField, group, projectField2);

        AggregationResults<Document> results = mongoTemplate.aggregate(pipeline, "game", Document.class);

        Document result = results.getUniqueMappedResult();

        return result;
      
    }


}