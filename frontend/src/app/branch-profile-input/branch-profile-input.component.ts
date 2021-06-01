import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { microService } from '../microService';

@Component({
  selector: 'app-branch-profile-input',
  templateUrl: './branch-profile-input.component.html',
  styleUrls: ['./branch-profile-input.component.css']
})
export class BranchProfileInputComponent implements OnInit {

  @Input() mservice!: microService;
  @Output() branch_profile_sent = new EventEmitter();

  profile: any;
  branchValue: any;
  profileValue: any;
  typeValue: any;
  constructor() { 
    
  }

  ngOnInit(): void {
  }
  sendBranchProfile(){
    

    if(this.profile==="default"){
      this.profileValue="";
    }
    else{
      this.profileValue=this.profile;
    }
    console.log(this.profile);
    console.log(this.branchValue);
    
    this.branch_profile_sent.emit({

      "typeValue": this.typeValue,
      "branchValue": this.branchValue,
      "profileValue": this.profileValue
    });
  }
  showFetchedService(){
    console.log(this.mservice.branches);
    console.log(this.branchValue);
    console.log(this.profileValue);
  }

}
