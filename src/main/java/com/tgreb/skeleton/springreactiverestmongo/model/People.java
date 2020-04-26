package com.tgreb.skeleton.springreactiverestmongo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Null;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document
public class People {
    @Id
    @Null
    private String id;
    private String name;
}
