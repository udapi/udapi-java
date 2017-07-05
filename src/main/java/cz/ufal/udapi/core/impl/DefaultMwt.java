package cz.ufal.udapi.core.impl;

import cz.ufal.udapi.core.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mvojtek on 05/07/2017.
 *
 * Represents multi-word token in UD tree.
 */
public class DefaultMwt implements Mwt {
    private List<Node> words = new ArrayList<>();
    private String form;
    private Misc misc;
    private Root root;

    @Override
    public String getForm() {
        return form;
    }

    @Override
    public Misc getMisc() {
        return misc;
    }

    @Override
    public void setWords(List<Node> words) {
        this.words.clear();
        if (null != words) {
            this.words.addAll(words);
        }
    }

    @Override
    public void setRoot(Root root) {
        this.root = root;
    }

    @Override
    public void setForm(String form) {
        this.form = form;
    }

    @Override
    public void setMisc(String misc) {
        this.misc = new DefaultMisc(misc);
    }

    @Override
    public String getOrdRange() {
        if (words.isEmpty()) {
            return "?-?";
        } else {
            return String.format("%d-%d", words.get(0).getOrd(), words.get(words.size()-1).getOrd());
        }
    }

    @Override
    public String getAddresss() {
        String rootAddress = "?";
        if (null != root) {
            rootAddress = root.getAddress();
        }
        return rootAddress + "#" + getOrdRange();
    }


}
