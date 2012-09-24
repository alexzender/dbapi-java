package dbapi.kernel.query.jpql;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class SimpleNode
implements Node, Serializable {

    final int id;
    final JPQL parser;
    SimpleNode parent;
    SimpleNode[] children;
    String text;
    boolean not = false;
    boolean inEnumPath = false;

    public SimpleNode(JPQL parser, int id) {
        this.id = id;
        this.parser = parser;
        this.inEnumPath = parser.inEnumPath;
    }

    public void jjtOpen() {
    }

    public void jjtClose() {
    }

    SimpleNode[] findChildrenByID(int id) {
        Collection<SimpleNode> set = new LinkedHashSet<SimpleNode>();
        findChildrenByID(id, set);
        return set.toArray(new SimpleNode[set.size()]);
    }

    private void findChildrenByID(int id, Collection<SimpleNode> set) {
        for (int i = 0; children != null && i < children.length; i++) {
            if (children[i].id == id)
                set.add(children[i]);

            children[i].findChildrenByID(id, set);
        }
    }

    boolean hasChildID(int id) {
        return findChildByID(id, false) != null;
    }

    SimpleNode findChildByID(int id, boolean recurse) {
        for (int i = 0; children != null && i < children.length; i++) {
            SimpleNode child = children[i];

            if (child.id == id)
                return children[i];

            if (recurse) {
                SimpleNode found = child.findChildByID(id, recurse);
                if (found != null)
                    return found;
            }
        }

        // not found
        return null;
    }

    public void jjtSetParent(Node parent) {
        this.parent = (SimpleNode) parent;
    }

    public Node jjtGetParent() {
        return this.parent;
    }

    public void jjtAddChild(Node n, int i) {
        if (children == null) {
            children = new SimpleNode[i + 1];
        } else if (i >= children.length) {
            SimpleNode c[] = new SimpleNode[i + 1];
            System.arraycopy(children, 0, c, 0, children.length);
            children = c;
        }

        children[i] = (SimpleNode) n;
    }

    public Node jjtGetChild(int i) {
        return children[i];
    }

    public int getChildCount() {
        return jjtGetNumChildren();
    }

    public SimpleNode getChild(int index) {
        return (SimpleNode) jjtGetChild(index);
    }

    public Iterator iterator() {
        return Arrays.asList(children).iterator();
    }

    public int jjtGetNumChildren() {
        return (children == null) ? 0 : children.length;
    }

    void setText(String text) {
        this.text = text;
    }

    void setToken(Token t) {
        setText(t.image);
    }

    public String toString() {
        return JPQLTreeConstants.jjtNodeName[this.id];
    }

    public String toString(String prefix) {
        return prefix + toString();
    }

    /**
     * Debugging method.
     *
     * @see #dump(java.io.PrintStream,String)
     */
    public void dump(String prefix) {
        dump(System.out, prefix);
    }

    public void dump() {
        dump(" ");
    }

    /**
     * Debugging method to output a parse tree.
     *
     * @param out the stream to which to write the debugging info
     * @param prefix the prefix to write out before lines
     */
    public void dump(PrintStream out, String prefix) {
        dump(out, prefix, false);
    }

    public void dump(PrintStream out, String prefix, boolean text) {
        out.println(toString(prefix)
            + (text && this.text != null ? " [" + this.text + "]" : ""));
        if (children != null) {
            for (int i = 0; i < children.length; ++i) {
                SimpleNode n = (SimpleNode) children[i];
                if (n != null) {
                    n.dump(out, prefix + " ", text);
                }
            }
        }
    }

    public SimpleNode getChildByID(int id, boolean recursive) 
    {   
        SimpleNode found = findChildByID(id, recursive);
        return found;
    }
    
    public void getChildrenByID(int id, Set<SimpleNode> res)
    {        
        for (int i = 0; children != null && i < children.length; i++)
        {
            SimpleNode q = children[i];
            if (children[i].id == id)
            {
                res.add(q);
            }
            q.getChildrenByID(id, res);            
        }        
    }
    
    public int getId()
    {
        return id;
    }
    
    
    public String getText()
    {
        return text;
    }
    
    public String getAlias()
    {
        return getChild(0).getChild(0).getText();
    }
    public String getColumn()
    {
        return getChild(0).getChild(1).getText();
    }
    
    public boolean isEQUALS()
    {
        return getId() == JPQLTreeConstants.JJTEQUALS;
    }
    public boolean isLIKE()
    {
        return getId() == JPQLTreeConstants.JJTLIKE;
    }
    public boolean isIN()
    {
        return getId() == JPQLTreeConstants.JJTIN;
    }
    
    public boolean isBETWEEN()
    {
        return getId() == JPQLTreeConstants.JJTBETWEEN;
    }
}
