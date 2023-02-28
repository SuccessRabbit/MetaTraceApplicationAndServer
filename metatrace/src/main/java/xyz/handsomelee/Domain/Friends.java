package xyz.handsomelee.Domain;


import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Friends {
    public Long id;
    public String senderId;
    public String receiverId;
    public Boolean isAccepted;
}
