package cz.ufal.udapi.core;

import java.util.List;

/**
 * Created by mvojtek on 05/07/2017.
 */
public interface EnhancedDeps {
    List<EnhancedDeps.Dep> getDeps();

    String toStringFormat();

    abstract class RootNode {
        public abstract boolean isRoot();
        public Root getRoot() {return null;}
        public Node getNode() {return null;}

        public int getOrd() {
            if (isRoot()) {
                return 0;
            } else {
                return getNode().getOrd();
            }
        }
    }

    class RootNodeDep extends RootNode {

        private final Root root;

        public RootNodeDep(Root root) {
            this.root = root;
        }

        @Override
        public boolean isRoot() {
            return true;
        }

        @Override
        public Root getRoot() {
            return root;
        }
    }

    class NodeDep extends RootNode {

        private final Node node;

        public NodeDep(Node node) {
            this.node = node;
        }

        @Override
        public boolean isRoot() {
            return false;
        }

        @Override
        public Node getNode() {
            return node;
        }
    }

    class Dep {

        private final RootNode head;
        private final String rel;

        public Dep(RootNode head, String rel) {
            this.head = head;
            this.rel = rel;
        }

        public RootNode getHead() {
            return head;
        }

        public String getRel() {
            return rel;
        }
    }
}
