<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

class CreateChats extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::connection('mysql2')->create('chats', function (Blueprint $table) {
            $table->increments('ID');
            $table->unsignedInteger('user_from');
            $table->unsignedInteger('user_to');
            $table->text('message');
            $table->text('msg_type');
            $table->integer('origin');
            $table->foreign('user_from')->references('ID')->on('chat_users')->onDelete('cascade');
            $table->foreign('user_to')->references('ID')->on('chat_users')->onDelete('cascade');
            $table->dateTime('time');
            // $table->timestamp('created_at')->default(\DB::raw('CURRENT_TIMESTAMP'));
            // $table->timestamp('updated_at')->default(\DB::raw('CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP'));
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::connection('mysql2')->dropIfExists('chats');
    }
}
