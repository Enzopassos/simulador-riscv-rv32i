public class Registers {
    private final int[] regs = new int[32];

    public Registers() {
        regs[0] = 0;
    }

    public int read(int regNum) {
        if (regNum < 0 || regNum >= 32) return 0;
        return regs[regNum];
    }

    public void write(int regNum, int value) {
        if (regNum != 0 && regNum < 32 && regNum >= 0) {
            regs[regNum] = value;
        }
    }

    public int[] getAll() {
    return regs.clone();
    }
    
    public void dump() {
        System.out.println("--- Registradores ---");
        for(int i=0; i<32; i++) {
            if(i % 4 == 0) System.out.println();
            System.out.printf("x%02d: 0x%08X  ", i, regs[i]);
        }
        System.out.println("\n---------------------");
    }
}