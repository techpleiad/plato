import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import {MatChipInputEvent} from '@angular/material/chips';
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import { microService } from '../microService';

export interface Profile {
  name: string;
}

export interface Branch {
  name: string;
  priority: number;
}

@Component({
  selector: 'app-add-new-service',
  templateUrl: './add-new-service.component.html',
  styleUrls: ['./add-new-service.component.css']
})
export class AddNewServiceComponent implements OnInit {
  //profile variables
  visible = true;
  selectable = true;
  removable = true;
  addOnBlur = true;
  readonly separatorKeysCodes = [ENTER, COMMA] as const;
  profiles: Profile[] = [];
  //profile variables

  //branch variables
  branches: Branch[] = [];
  //branch variables

  addService!: microService;
  isServiceValid = true;
  isUrlValid = true;
  isUsernameValid = true;
  isPasswordValid = true;
  checked = false;
  url: string="";
  username: string="";
  password: string="";

  //profile module chip fucntions
  add(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();

    // Add our fruit
    if (value) {
      this.profiles.push({name: value});
    }

    // Clear the input value
    event.chipInput!.clear();
  }

  remove(profile: Profile): void {
    const index = this.profiles.indexOf(profile);

    if (index >= 0) {
      this.profiles.splice(index, 1);
    }
  }
  //profile module chip functions

  //branch module chip fucntions
  addBranch(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();

    // Add our fruit
    if (value) {
      this.branches.push({name: value, priority: 0});
    }

    // Clear the input value
    event.chipInput!.clear();
  }

  removeBranch(branch: Branch): void {
    const index = this.branches.indexOf(branch);

    if (index >= 0) {
      this.branches.splice(index, 1);
    }
  }
  //branch module chip functions
  
  constructor(private dialogRef: MatDialogRef<AddNewServiceComponent>,@Inject(MAT_DIALOG_DATA) private data: microService) {
    this.addService=new microService();
    console.log(data);
  }

  addNewService(){
    for(let i=0;i<this.branches.length;i++){
      this.branches[i].priority=i+1;
    }
    this.addService.branches=this.branches;
    this.addService.profiles=this.profiles;
    this.isServiceValid=this.addService.service.length>0;
    this.isUrlValid=this.url.length>0;
    this.isUsernameValid=this.username.length>0;
    this.isPasswordValid=this.password.length>0;
    if(this.checked){
      this.addService.gitRepository={url: this.url};
      if(this.isServiceValid && this.isUrlValid){
        this.dialogRef.close(this.addService);
      }
    }else{
      this.addService.gitRepository={url: this.url, username: this.username, password: this.password};
      if(this.isServiceValid && this.isUrlValid && this.isUsernameValid && this.isPasswordValid){
        this.dialogRef.close(this.addService);
      }
    }
  }

  ngOnInit(): void {
  }

}
