public class BadFinalInit3 {
    
    void foo() {
        final int i;
        if (false) { i = 1; }
        i = 2;
    }
    
    public BadFinalInit3() { super(); }
}
