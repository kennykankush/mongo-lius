db.game.aggregate([
    {
        $lookup: {
            from: 'comments',
            localField: 'gid',
            foreignField: 'gid',
            as: 'comments',
            pipeline: [
                { $sort: { rating: -1 } },
                { $limit: 1 }
            ]
        }
    },
    {
        $unwind: '$comments'
    },
    {
        $project: {
            "gid": 1,
            "name": 1,
            rating: "$comments.rating",
            comment: "$comments.user",
            review_id: "$comments.c_id",
        }

    },
    
    {
        $group: {
            _id: null,
            rating: {$first: "highest"}, //{$push: "highest"} would give me in an array
            games: {$push: "$$ROOT"},
            timestamp: {$first: "$$NOW"}
            
        }
    },
    
    {
        $project: {
            "rating": 1,
            "games": 1,
            "timestamp":1 ,
            "_id": 0,
     
        }
    }
    
    


]);