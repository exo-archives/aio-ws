package org.mortbay.cometd.continuation;


public class EXoContinuationClient extends ContinuationClient {

    protected String eXoId;


    protected EXoContinuationClient(EXoContinuationBayeux bayeux) {
        super(bayeux);
    }

    public String getEXoId() {
        return eXoId;
    }

    public void setEXoId(String eXoId) {
        this.eXoId = eXoId;
    }
}
