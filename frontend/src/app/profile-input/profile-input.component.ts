import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { microService } from '../microService';

@Component({
  selector: 'app-profile-input',
  templateUrl: './profile-input.component.html',
  styleUrls: ['./profile-input.component.css']
})
export class ProfileInputComponent implements OnInit {
  @Input() mservice!: microService;
  @Input() canProfileDefault!: Boolean;
  @Output() profile_sent = new EventEmitter();

  constructor() { }

  ngOnInit(): void {
  }
  sendProfile(event: any){
    if(event.value==="default"){
      this.profile_sent.emit("");
    }
    else{
      this.profile_sent.emit(event.value);
    }
    
  }

}
