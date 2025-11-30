public class Memory {
    private final byte[] data = new byte[0xA0000]; 

    public int readByte(int address) {
        if (address < 0 || address >= data.length) return 0;
        return Byte.toUnsignedInt(data[address]);
    }

    public void writeByte(int address, int value) {
        if (address >= 0 && address < data.length) {
            data[address] = (byte) value;
        }
    }
    
    public int readWord(int address) {
        if (address + 3 >= data.length) return 0;
        int b0 = readByte(address);
        int b1 = readByte(address + 1);
        int b2 = readByte(address + 2);
        int b3 = readByte(address + 3);
        return b0 | (b1 << 8) | (b2 << 16) | (b3 << 24);
    }

    public void writeWord(int address, int value) {
        if (address + 3 >= data.length) return;
        writeByte(address, value & 0xFF);
        writeByte(address + 1, (value >> 8) & 0xFF);
        writeByte(address + 2, (value >> 16) & 0xFF);
        writeByte(address + 3, (value >> 24) & 0xFF);
    }
}