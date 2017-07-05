package cz.ufal.udapi.core.impl;

import cz.ufal.udapi.core.EnhancedDeps;
import cz.ufal.udapi.core.Root;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mvojtek on 05/07/2017.
 */
public class DefaultEnhancedDeps implements EnhancedDeps {

    private static final String PIPE = "|";
    private static final String PIPE_REGEX = "\\|";
    private static final String COLON = ":";
    private static final String UNDERSCORE = "_";

    private String stringRepresentation;
    private List<Dep> deps = new ArrayList<>();
    private Root root;

    public DefaultEnhancedDeps(String value, Root root) {
        setMapping(value);
        this.root = root;
    }

    @Override
    public List<Dep> getDeps() {

        if (deps.isEmpty()) {

            if (UNDERSCORE.equals(stringRepresentation)) {
                return deps;
            }

            Map<Integer, RootNode> map = new HashMap<>();
            map.put(0, new RootNodeDep(root));
            root.getDescendants().forEach(d -> map.put(d.getOrd(), new NodeDep(d)));

            String[] rawDeps = stringRepresentation.split(PIPE_REGEX);
            for (String rawDep : rawDeps) {

                String[] fields = rawDep.split(COLON);
                int head = Integer.parseInt(fields[0]);
                String rel = fields[1];
                deps.add(new Dep(map.get(head), rel));
            }

        }

        return deps;
    }

    public void setMapping(String value) {
        if (null == value) {
            deps.clear();
        } else {
            deps.clear();
            if ("".equals(value)) {
                stringRepresentation = UNDERSCORE;
            } else {
                stringRepresentation = value;
            }
        }
    }

    public void setMapping(List<Dep> value) {
        if (null == value) {
            deps.clear();
        } else {
            deps.clear();
            deps.addAll(value);
            stringRepresentation = null;
        }
    }

    @Override
    public String toStringFormat() {
        if (null == stringRepresentation) {
            //build string

            if (deps.isEmpty()) {
                stringRepresentation = UNDERSCORE;
            } else {
                StringBuilder sb = new StringBuilder();
                int i = 0;
                int size = deps.size();
                for (Dep item : deps) {
                    sb.append(item.getHead().getOrd());
                    sb.append(COLON);
                    sb.append(item.getRel());
                    if (i < size-1) {
                        sb.append(PIPE);
                    }
                    i++;
                }

                stringRepresentation = sb.toString();
            }

        }

        return stringRepresentation;
    }


}
