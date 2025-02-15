using System;
using System.Collections.Generic;

namespace DigitalDeivice.Models;

public partial class Home
{
    public string HomeId { get; set; } = null!;

    public string Name { get; set; } = null!;

    public string? Address { get; set; }

    public string? PhotoPath { get; set; }
    public string? UrlMqtt { get; set; }
    public string? UserMQTT { get; set; }
    public string? PasswordMQTT { get; set; }


    public virtual ICollection<HomeUser> HomeUsers { get; set; } = new List<HomeUser>();

    public virtual ICollection<Room> Rooms { get; set; } = new List<Room>();
}
