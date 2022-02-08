package jemu.core.cpu;

import jemu.core.*;
import jemu.core.device.*;

/**
 * Title:        JEMU
 * Description:  The Java Emulation Platform
 * Copyright:    Copyright (c) 2002
 * Company:
 * @author
 * @version 1.0
 */

public class MC6502 extends Processor {

  // =============================================================
  // Timings for instructions. This is standard MC6502 T-States.
  // =============================================================

  protected static final int NMI_MASK = 0xffff0000;
  protected static final int INT_MASK = 0x0000ffff;
  
  protected static final int FC = 0x01;
  protected static final int FZ = 0x02;
  protected static final int FI = 0x04;
  protected static final int FD = 0x08;
  protected static final int FB = 0x10;
  protected static final int FV = 0x40;
  protected static final int FN = 0x80;
  
  protected static final int BIT_MASK = ~(FN | FV | FZ);

  protected int A;
  protected int X;
  protected int Y;
  protected int S;
  protected int PC;
  protected int P;
  
  protected int interruptMask = NMI_MASK | INT_MASK;
  
  protected boolean ready = true;
  protected int interrupt;
  
  public MC6502(long cyclesPerSecond) {
    super("MC6502",cyclesPerSecond);
  }
  
  public void reset() {
    A = X = Y = S = P = 0xff;
    ready = true;
    interruptMask = NMI_MASK | INT_MASK;
    // interruptPending = 0; ???
    PC = readWord(0xfffc);
  }
  
  // This is copied to test interrupts at the correct stage
  public final void cycle() {
    interrupt = interruptPending;
    cycles++;
    if (cycleDevice != null)
      cycleDevice.cycle();
  }
  
  public final void setReady(boolean value) {
    ready = value;
  }
  
  public final int readByte(int address) {
    while (!ready) cycle();
    int result = memory.readByte(address);
    cycle();
    return result;
  }
  
  public final int setNZ(int in) {
    if (in == 0)
      P = (P & ~FN) | FZ | (in & FN);
    else
      P = (P & ~(FZ | FN)) | (in & FN);
    return in;
  }
  
  public final int fetch() {
    while (!ready) cycle();
    int result = memory.readByte(PC);
    PC = (PC + 1) & 0xffff;
    cycle();
    return result;
  }
  
  public final int fetchWord() {
    return fetch() | fetch() << 8;
  }
  
  public final int readPC() {
    return readByte(PC);
  }
  
  public final int writeByte(int addr, int value) {
    int result = memory.writeByte(addr,value);
    cycle();
    return result;
  }
  
  public final void push(int value) {
    memory.writeByte(0x100 + S,value);
    S = (S - 1) & 0xff;
    cycle();
  }
  
  public final void pushWord(int value) {
    push(value >> 8);
    push(value);
  }
  
  public final int popFirst() {
    readByte(0x100 + S);
    return readByte(0x100 + (S = (S + 1) & 0xff));
  }
  
  public final int pop() {
    return readByte(0x100 + (S = (S + 1) & 0xff));
  }
  
  public final int zprd(int index) {
    int zp = fetch();
    readByte(zp);
    return readByte((zp + index) & 0xff);
  }
  
  public final int zprmw(int index) {
    int zp = fetch();
    readByte(zp);
    return (zp + index) & 0xff;
  }
  
  public final int indx() {
    int zp = fetch();
    readByte(zp);
    zp = (zp + X) & 0xff;
    return readByte(zp) | readByte((zp + 1) & 0xff) << 8;
  }
  
  public final int indyrd() {
    int zp = fetch();                                           // Cycle 2
    int addr = readByte(zp) | readByte((zp + 1) & 0xff) << 8;   // Cycles 3 & 4
    zp = (addr & 0xff00) | ((addr + Y) & 0xff);
    addr = (addr + Y) & 0xffff;
    if (zp != addr) readByte(zp);
    return readByte(addr);
  }
  
  public final int indywr() {
    int zp = fetch();
    int addr = readByte(zp) | readByte((zp + 1) & 0xff) << 8;
    readByte((addr & 0xff00) | ((addr + Y) & 0xff));
    return (addr + Y) & 0xffff;
  }
  
  public final int absrd(int index) {
    int addr = fetchWord();
    int temp = (addr & 0xff00) | ((addr + index) & 0xff);
    addr = (addr + index) & 0xffff;
    if (temp != addr) readByte(temp);
    return readByte(addr);
  }
  
  public final int absrmw(int index) {
    int addr = fetchWord();
    readByte((addr & 0xff00) | ((addr + index) & 0xff));
    return (addr + index) & 0xffff;
  }
  
  protected final void setP(int value) {  // This is required for instructions which affect the I flag
    P = value | 0x20;
    if ((P & FI) == 0)
      interruptMask = interruptMask | INT_MASK;
    else
      interruptMask = interruptMask & NMI_MASK;
  }
  
  public void clearInterrupt(int mask) {
    super.clearInterrupt(mask);
    if ((interruptMask & NMI_MASK) == 0 && (interruptPending & NMI_MASK) == 0)
      interruptMask |= NMI_MASK;
  }
  
  public final void doInterrupt() {
    readPC();
    readPC();
    pushWord(PC);
    push(P & ~FB);
    P |= FI;
    if ((interrupt & interruptMask & NMI_MASK) != 0) {
      PC = readWord(0xfffa);
      interruptMask = 0;
    }
    else {
      PC = readWord(0xfffe);
      interruptMask &= NMI_MASK;
    }
  }
  
  public final void step() {
    if ((interrupt & interruptMask) != 0)
      doInterrupt();
    else
      step(fetch());
  }
  
  public final void illegal(int opcode) {
    System.out.println("Illegal Opcode: " + Util.hex((byte)opcode) + " at " + Util.hex((short)(PC - 1)));
  }

  protected final void step(int opcode) {
    switch(opcode) {
      case 0x00: fetch(); pushWord(PC); push(P |= FB); setP(P | FI); PC = readWord(0xfffe);     break;  // BRK            7  // TODO: P | FB?
      case 0x01: setNZ(A |= readByte(indx()));                                                  break;  // ORA (ind,X)    6
      case 0x05: setNZ(A |= readByte(fetch()));                                                 break;  // ORA zp         3
      case 0x06: asl(fetch());                                                                  break;  // ASL zp         5
      case 0x08: readPC(); push(P);                                                             break;  // PHP            3
      case 0x09: setNZ(A |= fetch());                                                           break;  // ORA #          2
      case 0x0a: readPC(); P = (A & 0x80) == 0 ? P & ~FC : P | FC; setNZ(A = (A << 1) & 0xff);  break;  // ASL A          2
      case 0x0d: setNZ(A |= readByte(fetchWord()));                                             break;  // ORA abs        4
      case 0x0e: asl(fetchWord());                                                              break;  // ASL abs        6
      case 0x10: if ((P & FN) == 0) branch(); else fetch();                                     break;  // BPL rel        2-4
      case 0x11: setNZ(A |= indyrd());                                                          break;  // ORA (ind),Y    5
      case 0x15: setNZ(A |= zprd(X));                                                           break;  // ORA zp,X       4
      case 0x16: asl(zprmw(X));                                                                 break;  // ASL zp,X       6
      case 0x18: readPC(); P &= ~FC;                                                            break;  // CLC            2
      case 0x19: setNZ(A |= absrd(Y));                                                          break;  // ORA abs,Y      4
      case 0x1d: setNZ(A |= absrd(X));                                                          break;  // ORA abs,X      4
      case 0x1e: asl(absrmw(X));                                                                break;  // ASL abs,X      7
      case 0x20: int tmp = fetch(); readByte(0x100 + S); pushWord(PC); PC = tmp | fetch() << 8; break;  // JSR abs        6
      case 0x21: setNZ(A &= readByte(indx()));                                                  break;  // AND (ind,X)    6
      case 0x24: bit(fetch());                                                                  break;  // BIT zp         3
      case 0x25: setNZ(A &= readByte(fetch()));                                                 break;  // AND zp         3
      case 0x26: rol(fetch());                                                                  break;  // ROL zp         5
      case 0x28: setP(popFirst());  /* TODO: Timing on the register set for EI,DI */            break;  // PLP            4
      case 0x29: setNZ(A &= fetch());                                                           break;  // AND #          2
      case 0x2a: readPC(); rola();                                                              break;  // ROL A          2
      case 0x2c: bit(fetchWord());                                                              break;  // BIT abs        4
      case 0x2d: setNZ(A &= readByte(fetchWord()));                                             break;  // AND abs        4
      case 0x2e: rol(fetchWord());                                                              break;  // ROL abs        6
      case 0x30: if ((P & FN) != 0) branch(); else fetch();                                     break;  // BMI            2-4
      case 0x31: setNZ(A &= indyrd());                                                          break;  // AND (ind),Y    5
      case 0x35: setNZ(A &= zprd(X));                                                           break;  // AND zp,X       4
      case 0x36: rol(zprmw(X));                                                                 break;  // ROL zp,X       6
      case 0x38: readPC(); P |= FC;                                                             break;  // SEC            2
      case 0x39: setNZ(A &= absrd(Y));                                                          break;  // AND abs,Y      4
      case 0x3d: setNZ(A &= absrd(X));                                                          break;  // AND abs,X      4
      case 0x3e: rol(absrmw(X));                                                                break;  // ROL abs,X      7
      case 0x40: readPC(); setP(popFirst()); PC = pop() | pop() << 8;                           break;  // RTI            6
      case 0x41: setNZ(A ^= readByte(indx()));                                                  break;  // EOR (ind,X)    6
      case 0x45: setNZ(A ^= readByte(fetch()));                                                 break;  // EOR zp         3
      case 0x46: lsr(fetch());                                                                  break;  // LSR zp         5
      case 0x48: readPC(); push(A);                                                             break;  // PHA            3
      case 0x49: setNZ(A ^= fetch());                                                           break;  // EOR #          2
      case 0x4a: readPC(); P = (A & 0x01) == 0 ? P & ~FC : P | FC; setNZ(A >>= 1);              break;  // LSR A          2
      case 0x4c: PC = fetchWord();                                                              break;  // JMP abs        3
      case 0x4d: setNZ(A ^= readByte(fetchWord()));                                             break;  // EOR abs        4
      case 0x4e: lsr(fetchWord());                                                              break;  // LSR abs        6
      case 0x50: if ((P & FV) == 0) branch(); else fetch();                                     break;  // BVC            2
      case 0x51: setNZ(A ^= indyrd());                                                          break;  // EOR (ind),Y    5
      case 0x55: setNZ(A ^= zprd(X));                                                           break;  // EOR zp,X       4
      case 0x56: lsr(zprmw(X));                                                                 break;  // LSR zp,X       6
      case 0x58: setP(P & ~FI); readPC();  /* TODO: Check the order (should be right) */        break;  // CLI            2
      case 0x59: setNZ(A ^= absrd(Y));                                                          break;  // EOR abs,Y      4
      case 0x5d: setNZ(A ^= absrd(X));                                                          break;  // EOR abs,X      4
      case 0x5e: lsr(absrmw(X));                                                                break;  // LSR abs,X      7
      case 0x60: readPC(); readByte(PC = (popFirst() | pop() << 8)); PC = (PC + 1) & 0xffff;    break;  // RTS            6
      case 0x61: adc(readByte(indx()));                                                         break;  // ADC (ind,X)    6
      case 0x65: adc(readByte(fetch()));                                                        break;  // ADC zp         3
      case 0x66: ror(fetch());                                                                  break;  // ROR zp         5
      case 0x68: readPC(); setNZ(A = popFirst());                                               break;  // PLA            4
      case 0x69: adc(fetch());                                                                  break;  // ADC #          2
      case 0x6a: readPC(); rora();                                                              break;  // ROR A          2
      case 0x6c: jmpind();                                                                      break;  // JMP (ind)      5
      case 0x6d: adc(readByte(fetchWord()));                                                    break;  // ADC abs        4
      case 0x6e: ror(fetchWord());                                                              break;  // ROR abs        6
      case 0x70: if ((P & FV) != 0) branch(); else fetch();                                     break;  // BVS            2
      case 0x71: adc(indyrd());                                                                 break;  // ADC (ind),Y    5
      case 0x75: adc(zprd(X));                                                                  break;  // ADC zp,X       4
      case 0x76: ror(zprmw(X));                                                                 break;  // ROR zp,X       6
      case 0x78: setP(P | FI); readPC(); /* TODO: As with CLI above */                          break;  // SEI            2
      case 0x79: adc(absrd(Y));                                                                 break;  // ADC abs,Y      4
      case 0x7d: adc(absrd(X));                                                                 break;  // ADC abs,X      4
      case 0x7e: ror(absrmw(X));                                                                break;  // ROR abs,X      7
      case 0x80: branch();                                                                      break;  // BRA            2
      case 0x81: writeByte(indx(),A);                                                           break;  // STA (ind,X)    6
      case 0x84: writeByte(fetch(),Y);                                                          break;  // STY zp         3
      case 0x85: writeByte(fetch(),A);                                                          break;  // STA zp         3
      case 0x86: writeByte(fetch(),X);                                                          break;  // STX zp         3
      case 0x88: readPC(); setNZ(Y = (Y - 1) & 0xff);                                           break;  // DEY            2
      case 0x8a: readPC(); setNZ(A = X);                                                        break;  // TXA            2
      case 0x8c: writeByte(fetchWord(),Y);                                                      break;  // STY abs        4
      case 0x8d: writeByte(fetchWord(),A);                                                      break;  // STA abs        4
      case 0x8e: writeByte(fetchWord(),X);                                                      break;  // STX abs        4
      case 0x90: if ((P & FC) == 0) branch(); else fetch();                                     break;  // BCC            2
      case 0x91: writeByte(indywr(),A);                                                         break;  // STA (ind),Y    6
      case 0x94: writeByte(zprmw(X),Y);                                                         break;  // STY zp,X       4
      case 0x95: writeByte(zprmw(X),A);                                                         break;  // STA zp,X       4
      case 0x96: writeByte(zprmw(Y),X);                                                         break;  // STX zp,Y       4
      case 0x98: readPC(); setNZ(A = Y);                                                        break;  // TYA            2
      case 0x99: writeByte(absrmw(Y),A);                                                        break;  // STA abs,Y      5
      case 0x9a: readPC(); S = X;                                                               break;  // TXS            2
      case 0x9c: writeByte(fetchWord(),0);                                                      break;  // STZ abs        4 (undoc)
      case 0x9d: writeByte(absrmw(X),A);                                                        break;  // STA abs,X      5
      case 0x9e: writeByte(absrmw(X),A & X);                                                    break;  // STAX abs,X     5 (undoc)
      case 0xa0: setNZ(Y = fetch());                                                            break;  // LDY #          2
      case 0xa1: setNZ(A = readByte(indx()));                                                   break;  // LDA (ind,X)    6
      case 0xa2: setNZ(X = fetch());                                                            break;  // LDX #          2
      case 0xa4: setNZ(Y = readByte(fetch()));                                                  break;  // LDY zp         3
      case 0xa5: setNZ(A = readByte(fetch()));                                                  break;  // LDA zp         3
      case 0xa6: setNZ(X = readByte(fetch()));                                                  break;  // LDX zp         3
      case 0xa8: readPC(); setNZ(Y = A);                                                        break;  // TAY            2
      case 0xa9: setNZ(A = fetch());                                                            break;  // LDA #          2
      case 0xaa: readPC(); setNZ(X = A);                                                        break;  // TAX            2
      case 0xac: setNZ(Y = readByte(fetchWord()));                                              break;  // LDY abs        4
      case 0xad: setNZ(A = readByte(fetchWord()));                                              break;  // LDA abs        4
      case 0xae: setNZ(X = readByte(fetchWord()));                                              break;  // LDX abs        4
      case 0xb0: if ((P & FC) != 0) branch(); else fetch();                                     break;  // BCS            2
      case 0xb1: setNZ(A = indyrd());                                                           break;  // LDA (ind),Y    5
      case 0xb4: setNZ(Y = zprd(X));                                                            break;  // LDY zp,X       4
      case 0xb5: setNZ(A = zprd(X));                                                            break;  // LDA zp,X       4
      case 0xb6: setNZ(X = zprd(Y));                                                            break;  // LDX zp,Y       4
      case 0xb8: readPC(); P &= ~FV;                                                            break;  // CLV            2
      case 0xb9: setNZ(A = absrd(Y));                                                           break;  // LDA abs,Y      4
      case 0xba: readPC(); setNZ(X = S);                                                        break;  // TSX            2
      case 0xbc: setNZ(Y = absrd(X));                                                           break;  // LDY abs,X      4
      case 0xbd: setNZ(A = absrd(X));                                                           break;  // LDA abs,X      4
      case 0xbe: setNZ(X = absrd(Y));                                                           break;  // LDX abs,Y      4
      case 0xc0: cp(Y,fetch());                                                                 break;  // CPY #          2
      case 0xc1: cp(A,readByte(indx()));                                                        break;  // CMP (ind,X)    6
      case 0xc4: cp(Y,readByte(fetch()));                                                       break;  // CPY zp         3
      case 0xc5: cp(A,readByte(fetch()));                                                       break;  // CMP zp         3
      case 0xc6: dec(fetch());                                                                  break;  // DEC zp         5
      case 0xc8: readPC(); setNZ(Y = (Y + 1) & 0xff);                                           break;  // INY            2
      case 0xc9: cp(A,fetch());                                                                 break;  // CMP #          2
      case 0xca: readPC(); setNZ(X = (X - 1) & 0xff);                                           break;  // DEX            2
      case 0xcc: cp(Y,readByte(fetchWord()));                                                   break;  // CPY abs        4
      case 0xcd: cp(A,readByte(fetchWord()));                                                   break;  // CMP abs        4
      case 0xce: dec(fetchWord());                                                              break;  // DEC abs        6
      case 0xd0: if ((P & FZ) == 0) branch(); else fetch();                                     break;  // BNE            2
      case 0xd1: cp(A,indyrd());                                                                break;  // CMP (ind),Y    5
      case 0xd5: cp(A,zprd(X));                                                                 break;  // CMP zp,X       4
      case 0xd6: dec(zprmw(X));                                                                 break;  // DEC zp,X       6
      case 0xd8: readPC(); P &= ~FD;                                                            break;  // CLD            2
      case 0xd9: cp(A,absrd(Y));                                                                break;  // CMP abs,Y      4
      case 0xdd: cp(A,absrd(X));                                                                break;  // CMP abs,X      4
      case 0xde: dec(absrmw(X));                                                                break;  // DEC abs,X      7
      case 0xe0: cp(X,fetch());                                                                 break;  // CPX #          2
      case 0xe1: sbc(readByte(indx()));                                                         break;  // SBC (ind,X)    6
      case 0xe4: cp(X,readByte(fetch()));                                                       break;  // CPX zp         3
      case 0xe5: sbc(readByte(fetch()));                                                        break;  // SBC zp         3
      case 0xe6: inc(fetch());                                                                  break;  // INC zp         5
      case 0xe8: readPC(); setNZ(X = (X + 1) & 0xff);                                           break;  // INX            2
      case 0xe9: sbc(fetch());                                                                  break;  // SBC #          2
      case 0xea: readPC();                                                                      break;  // NOP            2
      case 0xec: cp(X,readByte(fetchWord()));                                                   break;  // CPX abs        4
      case 0xed: sbc(readByte(fetchWord()));                                                    break;  // SBC abs        4
      case 0xee: inc(fetchWord());                                                              break;  // INC abs        6
      case 0xf0: if ((P & FZ) != 0) branch(); else fetch();                                     break;  // BEQ            2
      case 0xf1: sbc(indyrd());                                                                 break;  // SBC (ind),Y    5
      case 0xf5: sbc(zprd(X));                                                                  break;  // SBC zp,X       4
      case 0xf6: inc(zprmw(X));                                                                 break;  // INC zp,X       6
      case 0xf8: readPC(); P |= FD;                                                             break;  // SED            2
      case 0xf9: sbc(absrd(Y));                                                                 break;  // SBC abs,Y      4
      case 0xfd: sbc(absrd(X));                                                                 break;  // SBC abs,X      4
      case 0xfe: inc(absrmw(X));                                                                break;  // INC abs,X      7
      
      default:   illegal(opcode);                                                               break;
    }
  }
  
  protected final void asl(int addr) {    // 3 cycles
    int val = readByte(addr);
    writeByte(addr,val);  // Extra cycle
    P = (val & 0x80) == 0 ? P & ~FC : P | FC;
    writeByte(addr,setNZ((val << 1) & 0xff));
  }
  
  protected final void bit(int addr) {
    int val = readByte(addr);
    P = ((A & val) == 0 ? (P & BIT_MASK) | FZ : (P & BIT_MASK)) | (val & (FN | FV));            
  }
  
  protected final void rol(int addr) {
    int val = readByte(addr);
    writeByte(addr,val);
    int cy = P & FC; // Already bit 0
    P = (val & 0x80) == 0 ? P & ~FC : P | FC;
    writeByte(addr,setNZ(((val << 1) & 0xff) | cy));
  }
  
  protected final void rola() {
    int cy = P & FC;
    P = (A & 0x80) == 0 ? P & ~FC : P | FC;
    setNZ(A = ((A << 1) & 0xff) | cy);
  }
  
  protected final void lsr(int addr) {
    int val = readByte(addr);
    writeByte(addr,val);
    P = (val & 0x01) == 0 ? P & ~FC : P | FC;
    writeByte(addr,setNZ(val >> 1));
  }
  
  // TODO: Check this
  protected final void adc(int val) {
    int cy = P & FC;
    if ((P & FD) != 0) {
      if (((A + val + cy) & 0xff) == 0) P |= FZ; else P &= ~FZ;
      int tmp = (A & 0x0f) + (val & 0x0f) + cy;
      if (tmp > 9) tmp += 6;
      A &= 0xf0;
      val &= 0xf0;
      int signed = (byte)A + (byte)val + tmp;
      A += tmp + val;
      P = (P & ~(FN | FV | FC)) | (A & FN) | (signed < -128 || signed > 127 ? FV : 0);
      if (A >= 0xa0) {
        A -= 0xa0;
        P |= FC;
      }
    }
    else {
      int result = (byte)A + (byte)val + cy;
      A = (cy = A + val + cy) & 0xff;
      P = (P & ~(FN | FZ | FV | FC)) | (A == 0 ? FZ : 0) | ((A & 0x80) == 0 ? 0 : FN) |
        (cy >= 0x100 ? FC : 0) | (result < -128 || result > 127 ? FV : 0);
    }
  }
  
  // Why does the 6502 use inverted carry logic for subtract?
  // TODO: Check this, especially decimal overflow etc.
  protected final void sbc(int val) {
    int cy = 1 - (P & FC);
    if ((P & FD) != 0) {
      if (((A - val - cy) & 0xff) == 0) P |= FZ; else P &= ~FZ;
      int tmp = (A & 0x0f) + 0x100 - (val & 0x0f) - cy;
      if (tmp < 0x100) tmp -= 6;
      if (tmp < 0xf0) tmp += 0x10;
      A = tmp + (A & 0xf0) - (val & 0xf0);
      P = (P & ~(FN | FV)) | FC | (A & FN) |
        ((A & 0x1F0) > 0x17f || (A & 0x1f0) < 0x80 ? FV : 0);
      if ((A & 0xff00) == 0) {
        A = (A - 0x60) & 0xff;
        P &= ~FC;
      }
    }
    else {
      int result = (byte)A - (byte)val - cy;
      A = (cy = A - val - cy) & 0xff;
      P = P & ~(FN | FZ | FV | FC) | (A == 0 ? FZ : 0) | ((A & 0x80) == 0 ? 0 : FN) |
        (cy < 0 ? 0 : FC) | (result < -128 || result > 127 ? FV : 0);
    }
  }
  
  protected final void ror(int addr) {
    int val = readByte(addr);
    writeByte(addr,val);
    int cy = (P & FC) == 0 ? 0x00 : 0x80;
    P = (val & 0x01) == 0 ? P & ~FC : P | FC;
    writeByte(addr,setNZ((val >> 1) | cy));
  }
  
  protected final void rora() {
    int cy = (P & FC) == 0 ? 0x00 : 0x80;
    P = (A & 0x01) == 0 ? P & ~FC : P | FC;
    A = (A >> 1) | cy;
  }
  
  protected final void cp(int reg, int val) {
    P = (P & ~(FN | FZ | FC)) | (reg == val ? FZ : 0) | (reg < val ? 0 : FC) |
      (((reg - val) & 0x80) == 0 ? 0 : FN);
  }
  
  protected final void dec(int addr) {
    int val = readByte(addr);
    writeByte(addr,val);
    writeByte(addr,setNZ((val - 1) & 0xff));
  }
  
  protected final void inc(int addr) {
    int val = readByte(addr);
    writeByte(addr,val);
    writeByte(addr,setNZ((val + 1) & 0xff));
  }
  
  protected final void branch() {
    byte rel = (byte)fetch();                     // 2 cycles used
    readPC();                                     // 3 cycles used
    interrupt = 0;                                // delay by 1 cycle
    int newPC = (PC + rel) & 0xffff;
    PC = (PC & 0xff00) | ((PC + rel) & 0xff);
    if (PC != newPC) {
      fetch();                                    // 4 cycles used
      PC = newPC;
    }
  }
  
  protected final void jmpind() {
    int addr = fetchWord();
    // This is a 6502 indirect jump bug (feature?) - the way it works anyhow
    PC = readByte(addr) | readByte((addr & 0xff00) | ((addr + 1) & 0xff)) << 8;
  }
  
  public String getState() {
    return "A=" + Util.hex((byte)A) + ", X=" + Util.hex((byte)X) + ", Y=" +
      Util.hex((byte)Y) + ", S=" + Util.hex((byte)S) + ", PC=" + Util.hex((short)PC);
  }

  public void stepOver() {
    step();
  }

  protected static final Register[] REGISTERS = {
    new Register("A"),
    new Register("X"),
    new Register("Y"),
    new Register("S"),
    new Register("PC",16),
    new Register("P",8,"NV-BDIZC")
  };

  public Register[] getRegisters() {
    return REGISTERS;
  }
  
  public int getRegisterValue(int index) {
    switch(index) {
      case 0: return A;
      case 1: return X;
      case 2: return Y;
      case 3: return S;
      case 4: return PC;
      case 5: return P;
    }
    return 0;
  }
  
  public int getProgramCounter() {
    return PC;
  }
  
  public void setA(int value) {
    A = value & 0xff;
  }
  
  public void setX(int value) {
    X = value & 0xff;
  }
  
  public void setY(int value) {
    Y = value & 0xff;
  }
  
  public void setS(int value) {
    S = value & 0xff;
  }
  
  public void setPC(int value) {
    PC = value & 0xffff;
  }
  
  public void setStatus(int value) {
    setP(value & 0xff);
  }


  public void BreakPoint (int breaknumber, int address){
  }
  public int getBreakPoint (int breaknumber){
      return breaknumber;
  }
public void BreakCheck(){}
}