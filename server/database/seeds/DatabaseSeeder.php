<?php

use Illuminate\Database\Seeder;
use Illuminate\Support\Str;

class DatabaseSeeder extends Seeder
{
    /**
     * Seed the application's database.
     *
     * @return void
     */
    public function run()
    {
        // $this->call(UsersTableSeeder::class);
        DB::table('users')->insert([
            'name' => "admin",
            'email_verified_at' => now(),
            'email' => 'admin@gmail.com',
            'password' => bcrypt('password'),
            'api_token' => bcrypt('password'),
            'remember_token' => Str::random(10),
        ]);
        DB::table('info')->insert([
            'id' => "1",
            'infotxt' => '১০ ইঞ্চি (২৫ সেন্টিমিটার) দৈর্ঘ্যের চেয়ে ছোট ইলিশ “জাটকা” নামে পরিচিত। মার্চ-এপ্রিল মাসে জাটকার প্রাচুর্যতা সর্বোচ্চ  পরিমাণে পরিলক্ষিত হয়। ইলিশ মাছের সর্বোচ্চ প্রজনন মৌসুম সেপ্টেম্বর-অক্টোবর (আশ্বিন-কার্তিক) মাস। কারেন্ট জাল ইলিশ মাছের জন্য অত্যন্ত ক্ষতিকর। এ জাল দিয়ে ইলিশ মাছ ধরা থেকে বিরত থাকুন, ইলিশ সম্পদ বৃদ্ধিতে সহায়তা করুন।',
            'txtcolor' => '#000000',
            'bgcolor' => '#ffffff',
            'enable' => 0,
        ]);
        DB::connection('mysql2')->insert('INSERT INTO chat_users (ID,`name`, token) VALUES (1,"admin",?);', [bcrypt('adminapi')]);
    }
}
