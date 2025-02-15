using System;
using System.Collections.Generic;

namespace DigitalDeivice.Models;

public partial class History
{
    public string HistoryId { get; set; } = null!;

    public string? Action { get; set; }

    public DateTime Time { get; set; }

    public string? UserId { get; set; }

    public string? DeviceId { get; set; }

    public virtual Device? Device { get; set; }

    public virtual User? User { get; set; }
}
