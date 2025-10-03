package models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Mesages {
    private int masage_id;
    private int user_id;
    private int group_id;
    private String masage;
    private String data;
    private boolean in_pin;
}
