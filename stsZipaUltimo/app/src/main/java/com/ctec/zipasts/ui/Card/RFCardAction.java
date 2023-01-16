package com.ctec.zipasts.ui.Card;

import android.util.Log;

import com.cloudpos.DeviceException;
import com.cloudpos.OperationListener;
import com.cloudpos.OperationResult;
import com.cloudpos.POSTerminal;
import com.cloudpos.TimeConstants;
import com.cloudpos.card.CPUCard;
import com.cloudpos.card.Card;
import com.cloudpos.card.MifareCard;
import com.cloudpos.card.MifareUltralightCard;
import com.cloudpos.card.MoneyValue;
import com.cloudpos.rfcardreader.RFCardReaderDevice;
import com.cloudpos.rfcardreader.RFCardReaderOperationResult;
import com.ctec.zipasts.R;
import com.wizarpos.mvc.base.ActionCallback;
import java.util.Map;

public class RFCardAction extends ActionModel {

    private RFCardReaderDevice device = null;
    Card rfCard;
    int sectorIndex = 0;
    int blockIndex = 1;
    private int pinType_level3 = 2;
    private static boolean isOpened=false;
    @Override
    protected void doBefore(Map<String, Object> param, ActionCallback callback) {
        super.doBefore(param, callback);
        if (device == null) {
            device = (RFCardReaderDevice) POSTerminal.getInstance(mContext)
                    .getDevice("cloudpos.device.rfcardreader");
        }
    }

    public void open( RFCardReaderDevice device ) {
        try {
            device.open();
        } catch (DeviceException e) {
            e.printStackTrace();
        }
    }

    public void listenForCardPresent(RFCardReaderDevice device) {
        try {
            OperationListener listener = new OperationListener() {

                @Override
                public void handleResult(OperationResult arg0) {
                    if (arg0.getResultCode() == OperationResult.SUCCESS) {
                        rfCard = ((RFCardReaderOperationResult) arg0).getCard();
                        verifyKeyA();
                        readBlock();



                    } else {
                        //           sendFailedLog2(mContext.getString(R.string.find_card_failed));
                    }
                }
            };
            device.listenForCardPresent(listener, TimeConstants.FOREVER);
            //   sendSuccessLog("");
        } catch (DeviceException e) {
            e.printStackTrace();
            //    sendFailedLog(mContext.getString(R.string.operation_failed));
        }

    }

    public void waitForCardPresent(Map<String, Object> param, ActionCallback callback) {
        try {
            sendSuccessLog("");
            OperationResult operationResult = device.waitForCardPresent(TimeConstants.FOREVER);
            if (operationResult.getResultCode() == OperationResult.SUCCESS) {
                sendSuccessLog2(mContext.getString(R.string. find_card_succeed));
                rfCard = ((RFCardReaderOperationResult) operationResult).getCard();
            } else {
                sendFailedLog2(mContext.getString(R.string.find_card_failed));
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void listenForCardAbsent(Map<String, Object> param, ActionCallback callback) {
        try {
            OperationListener listener = new OperationListener() {

                @Override
                public void handleResult(OperationResult arg0) {
                    if (arg0.getResultCode() == OperationResult.SUCCESS) {
                        sendSuccessLog2(mContext.getString(R.string.absent_card_succeed));
                        rfCard = null;
                    } else {
                        sendFailedLog2(mContext.getString(R.string.absent_card_failed));
                    }
                }
            };
            device.listenForCardAbsent(listener, TimeConstants.FOREVER);
            sendSuccessLog("");
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void waitForCardAbsent(Map<String, Object> param, ActionCallback callback) {
        try {
            sendSuccessLog("");
            OperationResult operationResult = device.waitForCardAbsent(TimeConstants.FOREVER);
            if (operationResult.getResultCode() == OperationResult.SUCCESS) {
                sendSuccessLog2(mContext.getString(R.string.absent_card_succeed));
                rfCard = null;
            } else {
                sendFailedLog2(mContext.getString(R.string.absent_card_failed));
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void cancelRequest(Map<String, Object> param, ActionCallback callback) {
        try {
            device.cancelRequest();
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void getMode(Map<String, Object> param, ActionCallback callback) {
        try {
            int mode = device.getMode();
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " Mode = " + mode);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void setSpeed(Map<String, Object> param, ActionCallback callback) {
        try {
            device.setSpeed(460800);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void getSpeed(Map<String, Object> param, ActionCallback callback) {
        try {
            int speed = device.getSpeed();
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " Speed = " + speed);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }


    public void getProtocol(Map<String, Object> param, ActionCallback callback) {
        try {
            int protocol = rfCard.getProtocol();
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " Protocol = "
                    + protocol);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void getCardStatus(Map<String, Object> param, ActionCallback callback) {
        try {
            int cardStatus = rfCard.getCardStatus();
            sendSuccessLog(mContext.getString(R.string.operation_succeed) + " Card Status = "
                    + cardStatus);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void verifyKeyA() {
        byte[] key = new byte[]{
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF
        };
        try {

            boolean verifyResult = ((MifareCard) rfCard).verifyKeyA(sectorIndex, key);
            if(verifyResult){
                Log.d("Q2", "----KEY A---");
            }else{
                Log.d("Q2", "----KEY A ERROR---");

            }
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void verifyKeyB(Map<String, Object> param, ActionCallback callback) {
        byte[] key = new byte[]{
                (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF
        };
        try {

            boolean verifyResult = ((MifareCard) rfCard).verifyKeyB(sectorIndex, key);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void verify_level3(Map<String, Object> param, ActionCallback callback) {
        final byte[] arryKey = {
                (byte) 0x49, (byte) 0x45, (byte) 0x4D, (byte) 0x4B, (byte) 0x41, (byte) 0x45, (byte) 0x52, (byte) 0x42,
                (byte) 0x21, (byte) 0x4E, (byte) 0x41, (byte) 0x43, (byte) 0x55, (byte) 0x4F, (byte) 0x59, (byte) 0x46
        };
        try {
            boolean verifyLevel3Result = ((MifareUltralightCard) rfCard).verifyKey(arryKey);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }




    public void setTextView(String msg) {

        sendSuccessLog2(msg);

    }


    public void readBlock() {
        try {
            byte[] result = ((MifareCard) rfCard).readBlock(sectorIndex, blockIndex);
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }


    public void writeBlock(Map<String, Object> param, ActionCallback callback) {
        byte[] arryData = CommonUtil.createMasterKey(16);// 随机创造16个字节的数组
        try {
            ((MifareCard) rfCard).writeBlock(sectorIndex, blockIndex, arryData);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }



    public void writeValue(Map<String, Object> param, ActionCallback callback) {
        try {
            MoneyValue value = new MoneyValue(new byte[]{
                    (byte) 0x39
            }, 1024);
            ((MifareCard) rfCard).writeValue(sectorIndex, blockIndex, value);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void incrementValue(Map<String, Object> param, ActionCallback callback) {
        try {
            ((MifareCard) rfCard).increaseValue(sectorIndex, blockIndex, 10);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void decrementValue(Map<String, Object> param, ActionCallback callback) {
        try {
            ((MifareCard) rfCard).decreaseValue(sectorIndex, blockIndex, 10);
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }





    public void disconnect(Map<String, Object> param, ActionCallback callback) {
        try {
            sendNormalLog(mContext.getString(R.string.rfcard_remove_card));
            ((CPUCard) rfCard).disconnect();
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }

    public void close() {
        try {
            rfCard = null;
            device.close();
            sendSuccessLog(mContext.getString(R.string.operation_succeed));
        } catch (DeviceException e) {
            e.printStackTrace();
            sendFailedLog(mContext.getString(R.string.operation_failed));
        }
    }
}
