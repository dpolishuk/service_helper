package com.dp.example;
 /**
 * User: Dmitry Polishuk <dmitry.polishuk@gmail.com>
 * Date: 25.05.12
 * Time: 20:50
 */
interface IApiResultReceiver {
    void send(int resultCode, in Bundle resultData);
}