package xyz.handsomelee.Domain;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@TableName("userinfo")
public class User {

    private String id;
    private String nickname;
    private String password;
    private Integer level;
    private Integer money;
    private String description;
}
