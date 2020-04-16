<?php

use Illuminate\Http\Request;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
 */

Route::middleware('auth:api')->get('/user', function (Request $request) {
    return $request->user();
});
Route::post('imageupload', 'MainController@imageupload');
Route::post('dataupload', 'MainController@dataupload');
Route::post('dataedit', 'MainController@dataedit');
Route::post('addnotification', 'MainController@addnotification');
Route::post('editnotification', 'MainController@editnotification');
Route::post('editname', 'MainController@editname');
Route::post('updateinfo', 'MainController@addinfo');
Route::post('updateuser', 'MainController@updateuser');
Route::post('addnewuser', 'MainController@addnewuser');
Route::post('deleteuser', 'MainController@deleteuser');
Route::post('updatepageindex', 'MainController@updatepageindex');

Route::delete('datadelete', 'MainController@datadelete');
Route::delete('deletenotification', 'MainController@deletenotification');

Route::get('data', 'MainController@getdata');
Route::get('images', 'MainController@getimages');
Route::get('chatusers', 'MainController@chatusers');
Route::get('getchats/{id}', 'MainController@getchats');
Route::get('getnotifications', 'MainController@getnotifications');
Route::get('getinfo', 'MainController@getinfo');
Route::get('getusers', 'MainController@getusers');

Route::get('android', 'AndroidController@get');

// Route::middleware('auth:api')
