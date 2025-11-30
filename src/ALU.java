public class ALU {
    
    public int add(int a, int b) { return a + b; }
    public int sub(int a, int b) { return a - b; }
    
    public int and(int a, int b) { return a & b; }
    public int or(int a, int b)  { return a | b; }
    public int xor(int a, int b) { return a ^ b; }
    
    public int sll(int a, int b) { return a << (b & 0x1F); }
    public int srl(int a, int b) { return a >>> (b & 0x1F); }
    public int sra(int a, int b) { return a >> (b & 0x1F); }
    
    public int slt(int a, int b) { return (a < b) ? 1 : 0; }
    public int sltu(int a, int b) { 

        long ua = Integer.toUnsignedLong(a);
        long ub = Integer.toUnsignedLong(b);
        return (ua < ub) ? 1 : 0;
    }
}