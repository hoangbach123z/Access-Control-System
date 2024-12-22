package com.bachnh.accesscontrolsystem.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ViewData {
    String fxmlPath;
    String icon;
    String text;
    boolean isDefault;
}
