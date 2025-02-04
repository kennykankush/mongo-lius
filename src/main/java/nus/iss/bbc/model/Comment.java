package nus.iss.bbc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    private String id;
    private String cid;
    private String user;
    private int rating;
    private String c_text;
    private int gid;

}
