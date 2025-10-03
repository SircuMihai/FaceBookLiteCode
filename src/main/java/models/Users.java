package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Users {
    private int user_id;
    private String name;
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String picture;
    private String last_login;
    private boolean personal;
    private int [] friends;
}
