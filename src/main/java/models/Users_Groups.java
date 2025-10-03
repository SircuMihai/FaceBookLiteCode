package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Users_Groups {
    private int users_groups_id;
    private int user_id;
    private int group_id;
    boolean admin = false;
}
