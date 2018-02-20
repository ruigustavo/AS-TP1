public class Frame {

    long id0, id1,id2, id4, id5;
          double  id3;

    public Frame(){}

    public long getId0() {
        return id0;
    }                   //time

    public void setId0(long id0) {
        this.id0 = id0;
    }   //time

    public long getId1() { return id1; }                //speed

    public void setId1(long id1) {
        this.id1 = id1;
    }   //speed

    public long getId2() {
        return id2;
    }               //altitude

    public void setId2(long id2) {
        this.id2 = id2;
    }   //altitude

    public long getId4() {
        return id4;
    }               //temperature

    public void setId4(long id4) {
        this.id4 = id4;
    }   //temperature

    public long getId5() {
        return id5;
    }//pitch

    public void setId5(long id5) {
        this.id5 = id5;
    }   //pitch

    public double getId3() {
        return id3;
    }               //pressure

    public void setId3(double id3) {
        this.id3 = id3;
    }   //pressure
}
