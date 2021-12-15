package com.jht.bleconnect.common;

/**
 * 指令结构基类
 * （包含了所有指令都有的开头）
 */

public class BaseCmdStruct {
	/*
	YNC_WORD(new byte[]{0x55, (byte) 0xAA}, 2),
	TRANSACTION_ID(null, 2),
	LINGO_ID(null, 1),
	MESSAGE_ID(null, 1),
	MESSAGE_SIZE(null, 2),
	CHECKSUM(null, 2),
	DATA(null, 0);
	 */

    public BaseCmdStruct() {
    }

    public BaseCmdStruct(CmdType type) {
        this(type, false);
    }

    public BaseCmdStruct(CmdType type, boolean isAck) {
        if (type != null) {
            this.lingoId = type.getLingoId();
            if (isAck) {
                this.ackMessageId = type.getMessageId();
            } else {
                this.messageId = type.getMessageId();
            }
        }
    }

    @CField(start = 0, format = CField.Format.SHORT)
    protected short syncWord = 0x55aa;

    @CField(start = 2)
    protected short transactionId;

    @CField(start = 4)
    protected byte lingoId;

    @CField(start = 5)
    protected byte messageId;

    @CField(start = 6)
    protected short messageSize;

    @CField(start = 8)
    protected short checkSum;

    @CField(unbonded = true, start = 10, format = CField.Format.BYTE_ARRAY)
    protected byte[] data;

    @CField(unbonded = true, start = 10)
    protected byte ackMessageId;

    @CField(unbonded = true, start = 11)
    protected byte ack;

    public short getSyncWord() {
        return syncWord;
    }

    public void setSyncWord(short syncWord) {
        this.syncWord = syncWord;
    }

    public short getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(short transactionId) {
        this.transactionId = transactionId;
    }

    public byte getLingoId() {
        return lingoId;
    }

    public void setLingoId(byte lingoId) {
        this.lingoId = lingoId;
    }

    public byte getMessageId() {
        return messageId;
    }

    public void setMessageId(byte messageId) {
        this.messageId = messageId;
    }

    public short getMessageSize() {
        return messageSize;
    }

    public void setMessageSize(short messageSize) {
        this.messageSize = messageSize;
    }

    public short getCheckSum() {
        return checkSum;
    }

    public void setCheckSum(short checkSum) {
        this.checkSum = checkSum;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte getAckMessageId() {
        return ackMessageId;
    }

    public void setAckMessageId(byte ackMessageId) {
        this.ackMessageId = ackMessageId;
    }

    public byte getAck() {
        return ack;
    }

    public void setAck(byte ack) {
        this.ack = ack;
    }

    /**
     * 是否返回了ack错误码
     */
    public boolean isAckMessage() {
        return messageId == 0x00;
    }
}
