package pinup.backend.member.command.domain;

import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateMemberRequest {

    private String nickname;
    private Users.Gender gender;
    private Users.PreferredCategory preferredCategory;
    private Users.PreferredSeason preferredSeason;
    private LocalDate birthDate;
}

