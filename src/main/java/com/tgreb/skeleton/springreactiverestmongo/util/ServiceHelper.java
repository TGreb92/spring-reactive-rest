package com.tgreb.skeleton.springreactiverestmongo.util;

import lombok.experimental.UtilityClass;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.FeatureDescriptor;
import java.util.stream.Stream;

@UtilityClass
public class ServiceHelper {

    public <T, R> R merge(T source, R target) {
        if (source != null) {
            String[] ignoreFields = getNullPropertyNames(source);
            BeanUtils.copyProperties(source, target, ignoreFields);
        }
        return target;
    }

    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper wrappedSource = new BeanWrapperImpl(source);
        return Stream.of(wrappedSource.getPropertyDescriptors())
                .map(FeatureDescriptor::getName)
                .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
                .toArray(String[]::new);
    }
}
