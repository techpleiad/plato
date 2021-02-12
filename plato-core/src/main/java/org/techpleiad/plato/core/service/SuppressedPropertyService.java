package org.techpleiad.plato.core.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.techpleiad.plato.core.advice.ExecutionTime;
import org.techpleiad.plato.core.domain.PropertyTreeNode;
import org.techpleiad.plato.core.port.in.IFilterSuppressPropertyUseCase;
import org.techpleiad.plato.core.port.in.IGetSuppressPropertyUseCase;
import org.techpleiad.plato.core.port.out.ISuppressPropertyPersistencePort;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SuppressedPropertyService implements IFilterSuppressPropertyUseCase, IGetSuppressPropertyUseCase {

    @Autowired
    private ISuppressPropertyPersistencePort suppressPropertyPort;

    @Override
    public Map<String, List<String>> getSuppressedProperties(final String serviceName) {
        return suppressPropertyPort.getSuppressedProperties(serviceName);
    }

    @ExecutionTime
    @Override
    public List<String> filterSuppressedProperties(final List<String> suppressedPropertiesList, final List<String> missingProperties) {

        final PropertyTreeNode suppressPropertyTree = PropertyTreeNode.convertPropertiesToPropertyTree(suppressedPropertiesList);
        return missingProperties
                .stream()
                .filter(property -> !isPropertySuppressedIterative(suppressPropertyTree, property.split("\\."), 0))
                .collect(Collectors.toList());
    }

    private boolean isPropertySuppressedIterative(final PropertyTreeNode root, final String[] property, final int tempIndex) {

        PropertyTreeNode specificNode;
        int index = 0;

        PropertyTreeNode ptr = root;
        while (Objects.nonNull(ptr)) { // iterate while root is not null
            // Property and child properties are suppressed
            if (ptr.isLeaf()) {
                return true;
            }
            if (index >= property.length) break;

            specificNode = ptr.getChild(property[index++]);
            // Try a specific Node if exist
            if (Objects.nonNull(specificNode)) {
                ptr = specificNode;
            }
            // Try a generic Node if exist
            else ptr = ptr.getChild("*");
        }
        return false;
    }

    private boolean isPropertySuppressedRecursive(final PropertyTreeNode root, final String[] property, final int index) {
        if (root.isLeaf()) {
            return true;
        } else if (index >= property.length) {
            log.info("suppressed property : {}", String.join(".", property));
            return false;
        }
        final PropertyTreeNode specificNode = root.getChild(property[index]);
        if (!Objects.isNull(specificNode)) {
            return isPropertySuppressedRecursive(specificNode, property, index + 1);
        }
        final PropertyTreeNode genericNode = root.getChild("*");
        if (!Objects.isNull(genericNode)) {
            return isPropertySuppressedRecursive(genericNode, property, index + 1);
        }
        return false;
    }
}
