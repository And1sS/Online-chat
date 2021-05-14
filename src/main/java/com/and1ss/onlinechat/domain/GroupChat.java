package com.and1ss.onlinechat.domain;

import lombok.*;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "group_chat")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@DynamicInsert
public class GroupChat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @NonNull
    private String title;

    private String about;

    @OneToOne
    @JoinColumn(name = "creator_id", referencedColumnName = "id")
    private AccountInfo creator;

    @OneToMany(mappedBy = "groupChat", fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.MERGE})
    private List<GroupChatUser> groupChatUsers = new ArrayList<>();
}