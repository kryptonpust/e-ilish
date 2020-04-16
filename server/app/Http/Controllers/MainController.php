<?php

namespace App\Http\Controllers;

use App\DataModel;
use App\Http\Resources\PageResource;
use App\ImagesModel;
use App\InfoModel;
use App\NotificationModel;
use App\UserModel;
use DB;
use function GuzzleHttp\json_encode;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Str;

class MainController extends Controller
{
    public function __construct()
    {
        $this->middleware('auth:api');
    }
    public function imageupload(Request $request)
    {
        $image = new ImagesModel();
        $file = $request->file('image');
        $image->name = $file->getClientOriginalName();
        $path = '/uploads/' . $file->store('images', 'public_uploads');
        $image->path = $path;
        $image->save();
        return json_encode(array('location' => $path));
    }
    public function updatepageindex(Request $request)
    {
        $number = 1;
        foreach ($request->index as $value) {
            $com = DataModel::find($value);
            $com->index = $number++;
            $com->save();
        }
        return json_encode(array(
            'title' => 'Order change',
            'type' => 'success',
            'text' => 'Page order change successful',
            'error_code' => null,
        ));
    }
    public function dataupload(Request $request)
    {
        $data = new DataModel();
        $data->title = $request->title;
        $data->titlecolor = $request->titlecolor;
        $data->html = $request->data;
        $data->save();
        return json_encode(array(
            'title' => 'Saved',
            'type' => 'success',
            'message' => 'Page save successful',
            'error_code' => 'null',
        ));
    }
    public function dataedit(Request $request)
    {
        $data = DataModel::find($request->id);
        $data->title = $request->title;
        $data->titlecolor = $request->titlecolor;
        $data->html = $request->html;
        $data->save();
        return json_encode(array(
            'title' => 'Saved',
            'type' => 'success',
            'text' => 'Page edit successful',
            'error_code' => null,
        ));
    }
    public function datadelete(Request $request)
    {
        $data = DataModel::find($request->id);
        $data->delete();
        return json_encode(array(
            'title' => 'Delete',
            'type' => 'success',
            'text' => 'Page delete successful',
            'error_code' => null,
        ));
    }
    public function getdata()
    {
        return PageResource::collection(DataModel::orderBy('index')->get());
    }
    public function getimages()
    {
        return PageResource::collection(ImagesModel::all());
    }

    public function chatusers()
    {
        return DB::connection('mysql2')->select('SELECT * from chat_users where id != ?;', [1]);
    }
    public function getchats($id)
    {
        return DB::connection('mysql2')->select('SELECT `message`,`msg_type`,`origin` from chats where user_from = ? OR user_to =?;', [$id, $id]);
    }

    public function getnotifications()
    {
        return PageResource::collection(NotificationModel::all());
    }

    public function addnotification(Request $request)
    {
        // dd($request);
        $notification = new NotificationModel();
        $notification->date = $request->date;
        $notification->startTime = $request->startTime;
        $notification->header = $request->header;
        $notification->body = $request->body;
        $notification->save();
        return json_encode(array(
            'title' => 'Saved',
            'type' => 'success',
            'message' => 'Notification creation successful',
            'error_code' => 'null',
        ));

    }
    public function editnotification(Request $request)
    {
        $notification = NotificationModel::find($request->id);
        $notification->date = $request->date;
        $notification->startTime = $request->startTime;
        $notification->header = $request->header;
        $notification->body = $request->body;
        $notification->save();
        return json_encode(array(
            'title' => 'Saved',
            'type' => 'success',
            'message' => 'Edit Notification successful',
            'error_code' => 'null',
        ));
    }
    public function deletenotification(Request $request)
    {
        $notification = NotificationModel::find($request->id);
        $notification->delete();
        return json_encode(array(
            'title' => 'Deleted',
            'type' => 'success',
            'message' => 'Delete Notification successful',
            'error_code' => 'null',
        ));
    }
    public function editname(Request $request)
    {

        try
        {
            DB::connection('mysql2')->select('UPDATE chat_users SET `name` = ? where id = ?;', [$request->name, $request->id]);
            return json_encode(array(
                'title' => 'Rename',
                'type' => 'success',
                'message' => 'User Rename successful',
                'error_code' => 'null',
            ));

        } catch (\PDOException $e) {
            return json_encode(array(
                'title' => 'Rename',
                'type' => 'error',
                'message' => 'User Rename failed',
                'error_code' => 'null',
            ));
        }
    }
    public function addinfo(Request $request)
    {
        // dd($request);
        $info = InfoModel::find($request->id);
        $info->infotxt = $request->infotxt;
        $info->txtcolor = $request->txtcolor;
        $info->bgcolor = $request->bgcolor;
        $info->enable = $request->enable;
        // $info->time = $request->time;
        $info->save();
        return json_encode(array(
            'title' => 'Saved',
            'type' => 'success',
            'message' => 'Added Warning Text',
            'error_code' => 'null',
        ));

    }
    public function getinfo(Request $request)
    {
        // dd($request);
        return InfoModel::all();

    }
    public function getusers(Request $request)
    {
        // dd($request);
        if (Auth::user()->id == 1) {
            return UserModel::all();
        } else {
            return json_encode(array("0" => Auth::user()));
        }

    }
    public function addnewuser(Request $request)
    {
        if (Auth::user()->id == 1) {
            $user = new UserModel();
            $user->name = $request->name;
            $user->email = $request->email;
            $user->password = bcrypt($request->pass);
            $user->api_token = Str::random(60);
            $user->save();
            return json_encode(array(
                'title' => 'User Account',
                'type' => 'success',
                'message' => 'Account creation successful',
                'error_code' => 'null',
            ));
        } else {
            return json_encode(array(
                'title' => 'User Account',
                'type' => 'error',
                'message' => 'Unauthorized',
                'error_code' => 'logout',
            ));
        }
    }
    public function updateuser(Request $request)
    {
        $logged_user = Auth::user();
        if (Hash::check($request->cpass, $logged_user->password)) {
            if ($logged_user->id == 1) {
                $user = UserModel::find($request->id);
                $user->name = $request->name;
                $user->email = $request->email;
                if ($request->pass != null) {
                    $user->password = bcrypt($request->pass);
                }
                // $user->api_token = Str::random(60);
                $user->save();
                return json_encode(array(
                    'title' => 'User Account',
                    'type' => 'success',
                    'message' => 'Account update successful',
                    'error_code' => 'null',
                ));
            } elseif ($logged_user->id == $request->id) {
                $user = UserModel::find($logged_user->id);
                $user->name = $request->name;
                $user->email = $request->email;
                if ($request->pass != null) {
                    $user->password = bcrypt($request->pass);
                }
                // $user->api_token = Str::random(60);
                $user->save();
                return json_encode(array(
                    'title' => 'User Account',
                    'type' => 'success',
                    'message' => 'Account update successful',
                    'error_code' => 'null',
                ));
            }
        } else {
            return json_encode(array(
                'title' => 'User Account',
                'type' => 'error',
                'message' => 'Unauthorized',
                'error_code' => 'logout',
            ));
        }
    }
    public function deleteuser(Request $request)
    {
        $logged_user = Auth::user();
        if (Hash::check($request->cpass, $logged_user->password)) {
            if ($logged_user->id == 1) {
                $user = UserModel::find($request->id);
                $user->delete();
                return json_encode(array(
                    'title' => 'User Account',
                    'type' => 'success',
                    'message' => 'Account delete successful',
                    'error_code' => 'null',
                ));
            } elseif ($logged_user->id == $request->id) {
                $user = UserModel::find($logged_user->id);
                $user->delete();
                return json_encode(array(
                    'title' => 'User Account',
                    'type' => 'success',
                    'message' => 'Account delete successful',
                    'error_code' => 'logout',
                ));
            }
        } else {
            return json_encode(array(
                'title' => 'User Account',
                'type' => 'error',
                'message' => 'Unauthorized',
                'error_code' => 'logout',
            ));
        }
    }

}
