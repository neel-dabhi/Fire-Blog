 imagePath.putFile(mainImageURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                        {
                            imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Uri downloadUrl = uri;
                                    //Do what you want with the url
                                    Map<String,String> userMap =    new HashMap<>();
                                    userMap.put("name",username);
                                    userMap.put("image",downloadUrl.toString());

                                    firebaseFirestore.collection("Users").document(userid).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {

                                                Toast.makeText(SetupActivity.this,"User Settings are Updated" ,Toast.LENGTH_SHORT).show();
                                                Intent mainPage = new Intent(SetupActivity.this,MainActivity.class);
                                                startActivity(mainPage);
                                                finish();

                                            }else {

                                                String error = task.getException().toString();
                                                Toast.makeText(SetupActivity.this,"FireStore Error:" + error,Toast.LENGTH_SHORT).show();


                                            }
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }

                                    });
                                }
                                Toast.makeText(SetupActivity.this, "Upload Done", Toast.LENGTH_LONG).show();
                            }
                        });