package cz.ufal.udapi.core;

import java.util.List;

/**
 * Created by mvojtek on 05/07/2017.
 */
public interface Mwt {
    String getForm();

    Misc getMisc();

    void setWords(List<Node> words);

    void setRoot(Root root);

    void setForm(String form);

    void setMisc(String misc);

    String getOrdRange();

    String getAddresss();

    List<Node> getWords();

    String toStringFormat();
}
