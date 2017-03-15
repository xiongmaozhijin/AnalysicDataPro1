package mis.nanshchui.com.app;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by baoxing on 2016/10/21.
 */
public class SocketHelper {

    public final static byte CONNECT_SUCCESS = 0x00;
    public final static byte ERROR_FLAG = (byte) 0xFF;
    public final static byte ERROR_SOCKET_CONNECT = (byte) 0xFF;
    public final static byte ERROR_LOGIN = (byte) 0xFE;
    public final static byte ERROR_SOCKET_TIMEOUT = (byte) 0xFD;

    /**
     * 请求命令
     *
     * @param execCmd
     * @param validateCmd
     * @return <br/>
     * <p>
     * 0xFF FF: 网络连接错误
     * </p>
     * <p>
     * 0xFF FE: 登陆错误，可能qq号和ck不匹配；或其它未知情况
     * </p>
     * <p>
     * 0xFF FD: 返回超时，可能是命令有错
     * </p>
     * <p>
     * 0xCA ** **: 正确数据
     * </p>
     */
    @NotNull
    public static byte[] requestCmd(@NotNull byte[] execCmd,
                                    @NotNull byte[] validateCmd) {
        final String host = "183.61.38.179";
        final int port = 443;
        final int timeout = 2500;

        OutputStream os = null;
        InputStream is = null;
        Socket clientSocket = null;
        byte[] readBytes = null;
        byte[] tempBytes = new byte[8 * 1024];
        List<byte[]> resultDatas = new ArrayList<>();

        int readCnt;

        // StrHelperUtils.debugBytes("validateCmd=", validateCmd);
        // StrHelperUtils.debugBytes("execCmd=", execCmd);

//        StrHelperUtils.log("===>send cmd =%s",
//                StrHelperUtils.byte2HexStr(execCmd));

        try {
            // clientSocket = new Socket(host, port);
            clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(host, port), timeout);
            clientSocket.setSoTimeout(timeout);
            clientSocket.setReuseAddress(true);

            os = clientSocket.getOutputStream();
            is = clientSocket.getInputStream();

            os.write(validateCmd);
            os.flush();

            readCnt = is.read(tempBytes);

            // 有效登陆
            int j = 3;
            int totalCnt = 0;
            int posCnt = 0;
            if ((readCnt > 35) && (tempBytes[4] == 0x75)) {
                os.write(execCmd);
                os.flush();

                do {
                    readCnt = is.read(tempBytes);
                    readBytes = new byte[readCnt];
                    System.arraycopy(tempBytes, 0, readBytes, 0, readCnt);

                    totalCnt += readCnt;
                    resultDatas.add(readBytes);

                } while ((readCnt == 4096) && (--j > 0));

                readBytes = new byte[totalCnt];
                posCnt = 0;
                for (byte[] item : resultDatas) {
                    System.arraycopy(item, 0, readBytes, posCnt, item.length);
                    posCnt += item.length;
                }

            } else {
                readBytes = new byte[]{ERROR_FLAG, ERROR_LOGIN};

            }

        } catch (SocketTimeoutException e) {
//            e.printStackTrace();
            StrHelperUtils.logError("", "", "requestCmd",
                    "throw Exception msg=%s", e.getLocalizedMessage());

            readBytes = new byte[]{ERROR_FLAG, ERROR_SOCKET_TIMEOUT};

        } catch (IOException e) {
            // socket io error
//            e.printStackTrace();
            StrHelperUtils.logError("", "", "requestCmd",
                    "throw Exception msg=%s", e.getLocalizedMessage());
            readBytes = new byte[]{ERROR_FLAG, ERROR_SOCKET_CONNECT};

        } catch (Exception e) {
//            e.printStackTrace();
            StrHelperUtils.logError("", "", "requestCmd",
                    "throw Exception msg=%s", e.getLocalizedMessage());
            readBytes = new byte[]{ERROR_FLAG, ERROR_SOCKET_CONNECT};

        } finally {
            try {

                if (clientSocket != null) {
                    clientSocket.close();
                }

                if (os != null) {
                    os.close();
                }

                if (is != null) {
                    is.close();
                }

            } catch (IOException e) {
//                e.printStackTrace();
                StrHelperUtils.logError("", "", "requestCmd",
                        "throw Exception msg=%s", e.getLocalizedMessage());
            }
        }

        return readBytes;
    }

    /**
     * 请求命令
     *
     * @param execCmd
     * @param validateCmd
     * @return <br/>
     * <p>
     * 0xFF FF: 网络连接错误
     * </p>
     * <p>
     * 0xFF FE: 登陆错误，可能qq号和ck不匹配；或其它未知情况
     * </p>
     * <p>
     * 0xFF FD: 返回超时，可能是命令有错
     * </p>
     * <p>
     * 0xCA ** **: 正确数据
     * </p>
     */
    @NotNull
    public static byte[] requestCmd(@NotNull byte[] execCmd,
                                    @NotNull byte[] validateCmd, @Nullable byte[] terminalBytes,
                                    int forcesCnt) {
        final String host = "183.61.38.179";
        final int port = 443;
        final int timeout = 1300;

        OutputStream os = null;
        InputStream is = null;
        Socket clientSocket = null;
        byte[] readBytes = null;
        byte[] tempBytes = new byte[4 * 1024];
        List<byte[]> resultDatas = new ArrayList<>();

        int readCnt;
        final int _FORCECNT = forcesCnt;

        // StrHelperUtils.debugBytes("validateCmd=", validateCmd);
        // StrHelperUtils.debugBytes("execCmd=", execCmd);

        StrHelperUtils.log("===>send cmd =%s",
                StrHelperUtils.byte2HexStr(execCmd));

        try {
            // clientSocket = new Socket(host, port);
            clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(host, port), timeout);
            clientSocket.setSoTimeout(timeout);
            clientSocket.setReuseAddress(true);

            os = clientSocket.getOutputStream();
            is = clientSocket.getInputStream();

            os.write(validateCmd);
            os.flush();

            readCnt = is.read(tempBytes);

            // 有效登陆
            boolean find = false;
            int totalCnt = 0;
            int posCnt = 0;
            if ((readCnt > 35) && (tempBytes[4] == 0x75)) {
                os.write(execCmd);
                os.flush();

                do {
                    try {
                        readCnt = is.read(tempBytes);

                    } catch (IOException e) {
                        if (forcesCnt >= _FORCECNT) {
                            throw new IOException(e);
                        }

                    }

                    if (readCnt == -1) {
                        continue;
                    }

                    readBytes = new byte[readCnt];
                    System.arraycopy(tempBytes, 0, readBytes, 0, readCnt);

                    totalCnt += readCnt;
                    resultDatas.add(readBytes);

                    if (terminalBytes != null) {

                        if (readCnt >= 4) {
                            find = (readBytes[0] == terminalBytes[0])
                                    && (readBytes[1] == terminalBytes[1])
                                    && (readBytes[2] == terminalBytes[2])
                                    && (readBytes[3] == terminalBytes[3]);
                        }

                    }

                } while ((--forcesCnt > 0) && !find);

                readBytes = new byte[totalCnt];
                posCnt = 0;
                for (byte[] item : resultDatas) {
                    System.arraycopy(item, 0, readBytes, posCnt, item.length);
                    posCnt += item.length;
                }

            } else {
                readBytes = new byte[]{ERROR_FLAG, ERROR_LOGIN};

            }

        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            StrHelperUtils.logError("", "", "requestCmd",
                    "throw Exception msg=%s", e.getLocalizedMessage());

            readBytes = new byte[]{ERROR_FLAG, ERROR_SOCKET_TIMEOUT};

        } catch (IOException e) {
            // socket io error
            e.printStackTrace();
            StrHelperUtils.logError("", "", "requestCmd",
                    "throw Exception msg=%s", e.getLocalizedMessage());
            readBytes = new byte[]{ERROR_FLAG, ERROR_SOCKET_CONNECT};

        } catch (Exception e) {
            e.printStackTrace();
            StrHelperUtils.logError("", "", "requestCmd",
                    "throw Exception msg=%s", e.getLocalizedMessage());
            readBytes = new byte[]{ERROR_FLAG, ERROR_SOCKET_CONNECT};
        } finally {
            try {

                if (clientSocket != null) {
                    clientSocket.close();
                }

                if (os != null) {
                    os.close();
                }

                if (is != null) {
                    is.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return readBytes;
    }

    /**
     * 获取验证了的 Socket
     *
     * @return 0:socket 1:outputinstream 2:inputstream 3:flag
     */
    @NotNull
    public static Object[] getValidateClientSocket(String qqHex, String ckHex) {
        final String host = "183.61.38.179";
        final int port = 443;
        final int timeout = 2500;
        final int tryCnt = 3;

        Socket clientSocket = null;
        OutputStream os;
        InputStream is;
        Object[] ret = new Object[4];

        int readCnt;
        byte[] validateCmd;
        byte[] tempBytes;

        for (int i = 0; i < tryCnt; i++) {
            try {
//                clientSocket = new Socket(host, port);
//                clientSocket.setSoTimeout(timeout);
//                clientSocket.setReuseAddress(true);

            	clientSocket = new Socket();
                clientSocket.connect(new InetSocketAddress(host, port), timeout);
                clientSocket.setSoTimeout(timeout);
                clientSocket.setReuseAddress(true);
            	
                os = clientSocket.getOutputStream();
                is = clientSocket.getInputStream();

                tempBytes = new byte[1024];
                validateCmd = getValidateCmd(qqHex, ckHex);

                os.write(validateCmd);
                os.flush();

                readCnt = is.read(tempBytes);

                if (readCnt == -1) {
                    ret[3] = ERROR_SOCKET_CONNECT;
                }

                if (readCnt > 0) {
                    // 有效登陆
                    if ((readCnt > 35) && (tempBytes[4] == 0x75)) {
                        ret[0] = clientSocket;
                        ret[1] = os;
                        ret[2] = is;
                        ret[3] = CONNECT_SUCCESS;

                    } else {
                        ret[3] = ERROR_LOGIN;
                        if (clientSocket != null) {
                            try {
                                clientSocket.close();

                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }

                        }

                        clientSocket = null;
                        os = null;
                        is = null;

                    }
                }


            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                ret[3] = ERROR_SOCKET_TIMEOUT;

            } catch (IOException e) {
                e.printStackTrace();
                ret[3] = ERROR_SOCKET_CONNECT;

                if (clientSocket != null) {
                    try {
                        clientSocket.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }

                clientSocket = null;
                os = null;
                is = null;
            }


            if (null != ret[3] && ((byte)ret[3] == CONNECT_SUCCESS)) {
                break;
            }

        }

        return ret;
    }

    @Nullable
    public static byte[] writeCmd(@NotNull OutputStream os,
                                  @NotNull InputStream is, @NotNull String reqCmdHex,
                                  @Nullable byte[] termimalBytes, int tryCnt) {

        byte[] reqCmdBytes;
        byte[] rBytes;
        
    

        reqCmdBytes = StrHelperUtils.hexStr2Bytes(reqCmdHex);
        
        //TODO
        StrHelperUtils.logVerbose("cbx", "temp_debug", "writeCmd", "\n\r sendByte--->:\n\r%s",  StrHelperUtils.byte2HexStr(reqCmdBytes));
        
        
        rBytes = writeBytes(os, is, reqCmdBytes, termimalBytes, tryCnt);

        

        
        return rBytes;
    }



    @Nullable
    private static byte[] writeBytes(@NotNull OutputStream os,
                                     @NotNull InputStream is, @NotNull byte[] outputBytes, @Nullable byte[] terminalBytes, int tryCnt) {
        byte[] readBytes = null;

        try {        	
            os.write(outputBytes);
            os.flush();

            readBytes = readBytesSaft(is, terminalBytes, tryCnt);

        } catch (IOException e) {
            e.printStackTrace();
            readBytes = null;
        }

        return readBytes;
    }

    @Nullable
    private static byte[] readBytesSaft(@NotNull InputStream is,
                                        @Nullable byte[] terminalBytes, int tryCnt) {
        byte[] readBytes = null;
        byte[] tempBytes;
        int readCnt;

        tryCnt = Math.max(1, tryCnt);
        tryCnt = Math.min(3, tryCnt);

        for (int i = 0; i < tryCnt; i++) {
            try {
                tempBytes = new byte[1024 * 4];

                readCnt = is.read(tempBytes);

                if (readCnt == -1) {
                    break;
                }
                
                
                //TODO
                byte[] debug = new byte[readCnt];
                System.arraycopy(tempBytes, 0, debug, 0, readCnt);
                StrHelperUtils.logVerbose("cbx", "temp_debug", "writeCmd", "\n\r readByte<-----\n\r%s",   debug==null ? "null" : StrHelperUtils.byte2HexStr(debug));
                
                if (terminalBytes != null) {
                    int tLen = terminalBytes.length;
                    boolean find;

                    if (readCnt < tLen) {
                        continue;
                    }

                    find = true;
                    for (int j = 0; j < tLen; j++) {
                        if (tempBytes[j] != terminalBytes[j]) {
                            find = false;
                        }
                    }

                    if (find) {
                        readBytes = new byte[readCnt];
                        System.arraycopy(tempBytes, 0, readBytes, 0, readCnt);
                        break;

                    } else {
                        continue;

                    }


                } else {
                    readBytes = new byte[readCnt];
                    System.arraycopy(tempBytes, 0, readBytes, 0, readCnt);
                    
                }


            } catch (IOException e) {
                e.printStackTrace();

            }
        }

        return readBytes;
    }

    @NotNull
    public static byte[] getValidateCmd(@NotNull String qqHex,
                                        @NotNull String ckHex) {
        String validateCmd;

        validateCmd = "74 67 77 5F 6C 37 5F 66 6F 72 77 61 72 64 0D 0A 48 6F 73 74 3A 20 6D 2E 6D 65 69 67 75 69 2E 71 71 2E 63 6F 6D 3A 34 34 33 0D 0A 0D 0A CA 00 00 01 9F 00 00 01 80 01 02 01 "
                + qqHex
                + " 00 00 00 0B "
                + ckHex
                + " 00 56 00 17 00 81 4A 37 "
                + qqHex
                + " 00 00 00 00 01 65 00 00 00 01 00 00 "
                + qqHex
                + " 00 00 00 01 00 00 01 59 48 63 73 38 33 7A 2B 58 32 43 4B 34 70 75 61 32 6C 38 30 74 44 4B 6B 58 59 56 59 6A 6E 6A 48 51 63 38 5A 79 37 75 59 6E 4F 64 52 66 49 53 69 42 7A 37 6E 43 63 6A 35 56 48 35 52 6D 77 6F 6C 63 74 48 6D 48 70 55 75 57 72 4B 6B 61 37 37 61 68 62 6A 67 46 43 39 52 4C 38 56 65 79 66 59 57 54 4C 42 30 38 42 41 4B 37 51 4F 61 4E 4A 41 73 71 78 74 52 49 4E 39 58 34 73 32 6C 74 62 6F 48 6A 32 6F 6D 77 6D 51 69 78 6F 2B 33 56 4D 6A 58 64 45 38 51 77 77 6C 37 54 2F 54 31 47 58 53 64 6B 58 55 36 50 62 57 2F 36 71 64 51 6E 52 4E 72 6B 44 51 44 67 71 54 42 42 41 68 53 76 51 76 4A 39 39 49 39 78 64 50 6B 37 58 47 79 66 77 59 6A 2B 43 6B 78 44 32 43 4C 2F 7A 4B 61 31 51 50 57 61 53 75 76 77 35 46 4B 52 51 39 55 4E 50 35 42 77 4A 48 33 4D 2B 75 6F 52 58 6F 7A 6B 74 53 36 72 61 39 55 7A 68 33 4F 6A 4D 4E 79 5A 6B 42 54 5A 6B 42 55 52 78 48 49 2F 69 75 31 58 49 68 54 48 63 74 2F 6B 41 58 47 47 6D 33 42 50 64 55 34 63 44 31 37 36 69 46 64 51 53 68 78 2B 5A 7A 42 35 4E 6A 2F 55 39 55 75 6F 6D 6E 78 5A 2F 71 64 44 4D 38 78 34 77 51 3D 3D 00 00 00 00 04";

        byte[] resultByte = StrHelperUtils.hexStr2Bytes(validateCmd);

        return resultByte;
    }

    public static boolean isVertifyCorrect(Object[] objs) {
        return (objs != null) && (null != objs[3]) && ((byte)objs[3] == CONNECT_SUCCESS);
    }
    
    
    public static void closeSocket(Object[] objs) {
    	Socket socket = null;
    	OutputStream os = null;
    	InputStream is = null;
    	
    	if (objs !=null && (null != objs[3]) && ((byte)objs[3] == CONNECT_SUCCESS) ) {
    		socket = (Socket) objs[0];
    		os = (OutputStream) objs[1];
    		is = (InputStream) objs[2];
    		
    		cloesScoke(socket, os, is);
    	}
    }
    
    public static void cloesScoke(Socket socket, OutputStream os, InputStream is) {
        try {
        	if (socket != null) {
        		socket.close();
        		
        	}
        	
        	if (os != null) {
        		os.close();
        		
        	}
        	
        	if (is != null) {
        		is.close();
        		
        	}
        	
        } catch (IOException e) {
        	
        }
        
    }
    
    /**
     * 是否登录失效
     * @param objs
     * @return	true，登录失效
     */
    public static boolean isInValiedCk(Object[] objs) {
    	return (objs !=null) && (null != objs[3]) && ((byte)objs[3] == ERROR_LOGIN);
    }
    

    public static String getConnErrorMsg(Object[] objs) {
        String msg = "";
        if (null == objs || (null == objs[3])) {
            msg = "unknown";

        } else {
            if ((byte)objs[3] == CONNECT_SUCCESS) {
                msg = "连接成功";

            } else if ((byte)objs[3] == ERROR_SOCKET_TIMEOUT) {
                msg = "连接超时";

            } else if ((byte)objs[3] == ERROR_SOCKET_CONNECT) {
                msg = "网络IO错误";

            } else if ((byte)objs[3] == ERROR_LOGIN) {
                msg = "登录失效";
            } else {
                msg = "unknown2";

            }

        }

        return msg;
    }


}
