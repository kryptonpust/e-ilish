<?php

namespace App\Http\Controllers;

use App\DataModel;
use App\Http\Resources\PageResource;
use App\ImagesModel;
use App\InfoModel;
use App\NotificationModel;
use Illuminate\Http\Request;

class AndroidController extends Controller
{
    public function get(Request $request)
    {
        $resopnse = [];
        if ($request->has('check')) {
            return [
                'avail_data' => DataModel::all('id', 'updated_at'),
                'avail_notifications' => NotificationModel::all('id', 'updated_at'),
                'avail_info' => InfoModel::all('id', 'updated_at'),
            ];
        } else if ($request->has('data')) {
            if (preg_match('/^[0-9,]+$/', $request->data)) {
                $data = explode(',', $request->data);
                $resopnse['data'] = DataModel::whereIn('id', $data)->get();
            }
        }
        if ($request->has('noti')) {

            if (preg_match('/^[0-9,]+$/', $request->noti)) {
                $noti = explode(',', $request->noti);
                $resopnse['notifications'] = NotificationModel::whereIn('id', $noti)->get();
            }
        }
        if ($request->has('info')) {

            if (preg_match('/^[0-9,]+$/', $request->info)) {
                $info = explode(',', $request->info);
                $resopnse['info'] = InfoModel::find(1)->get();
            }
        }
        if (empty($resopnse)) {
            $resopnse['data'] = [];
        }
        return $resopnse;

    }
    public function getimages()
    {
        return PageResource::collection(ImagesModel::all());
    }
}
