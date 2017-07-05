package cz.ufal.udapi.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mvojtek on 05/07/2017.
 *
 * Represents multi-word token in UD tree.
 */
public class Mwt {
    private List<Node> words = new ArrayList<>();
    private String form;
    private Misc misc;
    private Root root;

    public String getForm() {
        return form;
    }

    public Misc getMisc() {
        return misc;
    }

    public void setWords(List<Node> words) {
        this.words.clear();
        if (null != words) {
            this.words.addAll(words);
        }
    }

    public void setRoot(Root root) {
        this.root = root;
    }

    public void setForm(String form) {
        this.form = form;
    }

    public void setMisc(String misc) {
        this.misc = new Misc(misc);
    }

    public String getOrdRange() {
        if (words.isEmpty()) {
            return "?-?";
        } else {
            return String.format("%d-%d", words.get(0).getOrd(), words.get(words.size()-1).getOrd());
        }
    }

    public String getAddresss() {
        String rootAddress = "?";
        if (null != root) {
            rootAddress = root.getAddress();
        }
        return rootAddress + "#" + getOrdRange();
    }


}
