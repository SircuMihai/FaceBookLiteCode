package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Coments {
    private int comment_id;
    private int post_id;
    private int user_id;
    private int content;
}
