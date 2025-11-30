public class Bus {
    private final Memory memory;
    
    private static final int RAM_START  = 0x00000;
    private static final int RAM_END    = 0x7FFFF;
    private static final int VRAM_START = 0x80000;
    private static final int VRAM_END   = 0x8FFFF;
    private static final int IO_START   = 0x9FC00;

    public Bus(Memory memory) {
        this.memory = memory;
    }

    public int load(int address) {
        if (isValidAddress(address)) {
            return memory.readWord(address);
        }
        throw new RuntimeException("Segmentation Fault (Load Word): " + Integer.toHexString(address));
    }

    public int loadByte(int address) {
        if (isValidAddress(address)) {
            return memory.readByte(address);
        }
        throw new RuntimeException("Segmentation Fault (Load Byte): " + Integer.toHexString(address));
    }
    
    public int loadHalf(int address) {
        if (isValidAddress(address)) {
            int b0 = memory.readByte(address);
            int b1 = memory.readByte(address + 1);
            return b0 | (b1 << 8);
        }
        throw new RuntimeException("Segmentation Fault (Load Half): " + Integer.toHexString(address));
    }

    public void store(int address, int value) {
        if (address >= RAM_START && address <= RAM_END) {
            memory.writeWord(address, value);
        } 
        else if (address >= VRAM_START && address <= VRAM_END) {
            memory.writeWord(address, value);
        } 
        else if (address >= IO_START) {
            System.out.print((char)value); 
        }
    }

    public void storeByte(int address, int value) {
        if (address >= RAM_START && address <= VRAM_END) {
            memory.writeByte(address, value);
        }
        else if (address >= IO_START) {
            System.out.print((char)value);
        }
    }
    
    public void storeHalf(int address, int value) {
        if (address >= RAM_START && address <= VRAM_END) {
            memory.writeByte(address, value & 0xFF);
            memory.writeByte(address + 1, (value >> 8) & 0xFF);
        }
    }
    
    private boolean isValidAddress(int address) {
        return (address >= RAM_START && address <= IO_START + 0x400);
    }
    
    public void dumpVRAM() {
        System.out.println("\n--- VRAM OUTPUT ---");
        for (int addr = VRAM_START; addr <= VRAM_END; addr += 4) {
            int val = memory.readWord(addr);
            if (val != 0) { 
                System.out.print((char)val);
            }
        }
        System.out.println("\n-------------------");
    }

    public String getVRAMString() {
        StringBuilder sb = new StringBuilder();
        for (int addr = VRAM_START; addr <= VRAM_END; addr += 4) {
            int val = memory.readWord(addr);
            if (val != 0) {
                sb.append((char)val);
            }
        }
        return sb.toString();
    }
}